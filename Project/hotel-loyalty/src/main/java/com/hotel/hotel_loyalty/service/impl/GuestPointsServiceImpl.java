package com.hotel.hotel_loyalty.service.impl;

import com.hotel.hotel_loyalty.domain.OrderDO;
import com.hotel.hotel_loyalty.domain.PaymentMethodDO;
import com.hotel.hotel_loyalty.domain.PointsTransactionDO;
import com.hotel.hotel_loyalty.domain.ProductDO;
import com.hotel.hotel_loyalty.domain.UserDO;
import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.enums.OrderStatusEnum;
import com.hotel.hotel_loyalty.enums.PaymentTypeEnum;
import com.hotel.hotel_loyalty.mapper.OrderMapper;
import com.hotel.hotel_loyalty.mapper.PaymentMethodMapper;
import com.hotel.hotel_loyalty.mapper.PointsTransactionMapper;
import com.hotel.hotel_loyalty.mapper.ProductMapper;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import com.hotel.hotel_loyalty.payment.PaymentRequest;
import com.hotel.hotel_loyalty.payment.PaymentResult;
import com.hotel.hotel_loyalty.payment.PaymentStrategy;
import com.hotel.hotel_loyalty.payment.PaymentStrategyFactory;
import com.hotel.hotel_loyalty.service.GuestPointsService;
import com.hotel.hotel_loyalty.vo.ConfirmPaymentVO;
import com.hotel.hotel_loyalty.vo.AccountVO;
import com.hotel.hotel_loyalty.vo.MyPointsVO;
import com.hotel.hotel_loyalty.vo.PaymentMethodVO;
import com.hotel.hotel_loyalty.vo.PointsHistoryVO;
import com.hotel.hotel_loyalty.vo.PointsMallVO;
import com.hotel.hotel_loyalty.vo.ProductVO;
import com.hotel.hotel_loyalty.vo.RedemptionRecordVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Guest points mall and redemption service implementation.
 *
 * @author Copilot
 * @since 1.0
 */
@Service
public class GuestPointsServiceImpl implements GuestPointsService {

    /** One product is redeemed per order. */
    private static final int SINGLE_ITEM_QUANTITY = 1;
    /** Number of UUID characters used in generated business keys. */
    private static final int IDENTIFIER_SUFFIX_LENGTH = 8;
    /** Prefix for generated order business keys. */
    private static final String ORDER_ID_PREFIX = "O-";
    /** Prefix for generated points transaction business keys. */
    private static final String POINTS_TRANSACTION_ID_PREFIX = "PT-";
    /** Audit type for points consumed by a redemption. */
    private static final String REDEEM_TRANSACTION_TYPE = "REDEEM";

    /** User data access object. */
    private final UserMapper userMapper;
    /** Product data access object. */
    private final ProductMapper productMapper;
    /** Payment method data access object. */
    private final PaymentMethodMapper paymentMethodMapper;
    /** Order data access object. */
    private final OrderMapper orderMapper;
    /** Points transaction data access object. */
    private final PointsTransactionMapper pointsTransactionMapper;
    /** Payment strategy resolver. */
    private final PaymentStrategyFactory paymentStrategyFactory;

    /**
     * Creates the guest points service.
     *
     * @param userMapper user data access object
     * @param productMapper product data access object
     * @param paymentMethodMapper payment method data access object
     * @param orderMapper order data access object
     * @param pointsTransactionMapper points transaction data access object
     * @param paymentStrategyFactory payment strategy resolver
     */
    public GuestPointsServiceImpl(UserMapper userMapper, ProductMapper productMapper,
                                  PaymentMethodMapper paymentMethodMapper, OrderMapper orderMapper,
                                  PointsTransactionMapper pointsTransactionMapper,
                                  PaymentStrategyFactory paymentStrategyFactory) {
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.paymentMethodMapper = paymentMethodMapper;
        this.orderMapper = orderMapper;
        this.pointsTransactionMapper = pointsTransactionMapper;
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    /** {@inheritDoc} */
    @Override
    public AccountVO getAccount(String uid) {
        UserDO user = findUser(uid);
        AccountVO vo = new AccountVO();
        vo.setUid(user.getUid());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setLevelLabel("L" + user.getLevel());
        vo.setCurrentPoint(user.getCurrentPoint());
        vo.setRegisterTime(user.getRegisterTime());
        return vo;
    }

    /** {@inheritDoc} */
    @Override
    public PointsMallVO getPointsMall(String uid) {
        UserDO user = findUser(uid);
        PointsMallVO vo = new PointsMallVO();
        vo.setCurrentPoint(user.getCurrentPoint());
        vo.setProducts(productMapper.selectAvailable().stream().map(this::toProductVO).toList());
        return vo;
    }

    /** {@inheritDoc} */
    @Override
    public MyPointsVO getMyPoints(String uid) {
        UserDO user = findUser(uid);
        MyPointsVO vo = new MyPointsVO();
        vo.setName(user.getName());
        vo.setLevelLabel("L" + user.getLevel());
        vo.setCurrentPoint(user.getCurrentPoint());
        vo.setRegisterTime(user.getRegisterTime());
        vo.setRedemptions(orderMapper.selectByUid(uid).stream().map(this::toRedemptionVO).toList());
        vo.setHistory(pointsTransactionMapper.selectByUid(uid).stream().map(this::toHistoryVO).toList());
        return vo;
    }

    /** {@inheritDoc} */
    @Override
    public ConfirmPaymentVO getConfirmPayment(String uid, String pid) {
        UserDO user = findUser(uid);
        ProductDO product = findProduct(pid);
        ConfirmPaymentVO vo = new ConfirmPaymentVO();
        vo.setPid(product.getPid());
        vo.setName(product.getName());
        vo.setImageUrl(product.getImageUrl());
        vo.setTotalPointsRequired(product.getTotalPointsRequired());
        vo.setMinPointsRequired(product.getMinPointsRequired());
        vo.setAvailableBalance(user.getCurrentPoint());
        vo.setPaymentMethods(paymentMethodMapper.selectAll().stream().map(this::toPaymentMethodVO).toList());
        return vo;
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public String redeem(RedemptionRequest request) {
        UserDO user = findUser(request.getUid());
        ProductDO product = findProduct(request.getPid());
        PaymentMethodDO method = paymentMethodMapper.selectByPmid(request.getPmid());
        if (method == null) {
            throw new IllegalArgumentException("payment method not found: " + request.getPmid());
        }
        if (product.getRemainingStock() < SINGLE_ITEM_QUANTITY) {
            throw new IllegalStateException("product is out of stock: " + product.getPid());
        }

        PaymentTypeEnum paymentType = PaymentTypeEnum.fromPmid(request.getPmid());
        if (paymentType == null) {
            throw new IllegalArgumentException("unsupported payment method: " + request.getPmid());
        }
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(paymentType);
        PaymentResult validation = strategy.validate(new PaymentRequest(request.getUid(), request.getPid(),
                request.getPmid(), request.getPointsUsed(), request.getCashUsed(),
                request.getVoucherUsed(), product.getTotalPointsRequired(),
                product.getMinPointsRequired(), user.getCurrentPoint()));
        if (!validation.isSuccess()) {
            throw new IllegalArgumentException(validation.getMessage());
        }

        String oid = ORDER_ID_PREFIX + UUID.randomUUID().toString()
                .substring(0, IDENTIFIER_SUFFIX_LENGTH).toUpperCase();
        LocalDateTime now = LocalDateTime.now();
        OrderDO order = new OrderDO();
        order.setOid(oid);
        order.setUid(request.getUid());
        order.setPid(request.getPid());
        order.setPmid(request.getPmid());
        order.setPointsUsed(request.getPointsUsed());
        order.setCashUsed(request.getCashUsed());
        order.setVoucherUsed(request.getVoucherUsed());
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setGmtCreate(now);
        order.setGmtModified(now);
        orderMapper.insert(order);

        if (userMapper.compareAndUpdatePoint(request.getUid(), user.getCurrentPoint(),
                user.getCurrentPoint() - request.getPointsUsed(), now) != SINGLE_ITEM_QUANTITY) {
            throw new IllegalStateException("points balance changed; please retry");
        }
        if (productMapper.deductStock(request.getPid(), SINGLE_ITEM_QUANTITY, now)
                != SINGLE_ITEM_QUANTITY) {
            throw new IllegalStateException("product is no longer in stock");
        }
        if (orderMapper.compareAndUpdateStatus(oid, OrderStatusEnum.PENDING_PAYMENT.getCode(),
                OrderStatusEnum.PAID.getCode(), now) != SINGLE_ITEM_QUANTITY) {
            throw new IllegalStateException("could not complete payment");
        }

        PointsTransactionDO transaction = new PointsTransactionDO();
        transaction.setPtid(POINTS_TRANSACTION_ID_PREFIX + UUID.randomUUID().toString()
                .substring(0, IDENTIFIER_SUFFIX_LENGTH).toUpperCase());
        transaction.setUid(request.getUid());
        transaction.setRelatedOrderId(oid);
        transaction.setPointsChange(-request.getPointsUsed());
        transaction.setType(REDEEM_TRANSACTION_TYPE);
        transaction.setGmtCreate(now);
        transaction.setGmtModified(now);
        pointsTransactionMapper.insert(transaction);
        return oid;
    }

    /**
     * Finds a guest by business key.
     *
     * @param uid guest business key
     * @return user data object
     * @throws IllegalArgumentException when the user does not exist
     */
    private UserDO findUser(String uid) {
        UserDO user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new IllegalArgumentException("user not found: " + uid);
        }
        return user;
    }

    /**
     * Finds a product by business key.
     *
     * @param pid product business key
     * @return product data object
     * @throws IllegalArgumentException when the product does not exist
     */
    private ProductDO findProduct(String pid) {
        ProductDO product = productMapper.selectByPid(pid);
        if (product == null) {
            throw new IllegalArgumentException("product not found: " + pid);
        }
        return product;
    }

    /**
     * Converts a product data object into a mall view object.
     *
     * @param product product data object
     * @return product view object
     */
    private ProductVO toProductVO(ProductDO product) {
        ProductVO vo = new ProductVO();
        vo.setPid(product.getPid());
        vo.setName(product.getName());
        vo.setImageUrl(product.getImageUrl());
        vo.setTotalPointsRequired(product.getTotalPointsRequired());
        return vo;
    }

    /**
     * Converts a payment method data object into a view object.
     *
     * @param method payment method data object
     * @return payment method view object
     */
    private PaymentMethodVO toPaymentMethodVO(PaymentMethodDO method) {
        PaymentMethodVO vo = new PaymentMethodVO();
        vo.setPmid(method.getPmid());
        vo.setName(method.getName());
        return vo;
    }

    /**
     * Converts an order and its product into a redemption record.
     *
     * @param order order data object
     * @return redemption record view object
     */
    private RedemptionRecordVO toRedemptionVO(OrderDO order) {
        ProductDO product = findProduct(order.getPid());
        RedemptionRecordVO vo = new RedemptionRecordVO();
        vo.setOid(order.getOid());
        vo.setPid(order.getPid());
        vo.setProductName(product.getName());
        vo.setPointsUsed(order.getPointsUsed());
        vo.setCashUsed(order.getCashUsed());
        vo.setVoucherUsed(order.getVoucherUsed());
        vo.setStatus(order.getStatus());
        OrderStatusEnum statusEnum = OrderStatusEnum.fromCode(order.getStatus());
        vo.setStatusLabel(statusEnum != null ? statusEnum.getDescription() : "Unknown");
        vo.setGmtCreate(order.getGmtCreate());
        return vo;
    }

    /**
     * Converts a points transaction data object into a history view object.
     *
     * @param txn points transaction data object
     * @return points history view object
     */
    private PointsHistoryVO toHistoryVO(PointsTransactionDO txn) {
        PointsHistoryVO vo = new PointsHistoryVO();
        vo.setPtid(txn.getPtid());
        vo.setType(txn.getType());
        vo.setPointsChange(txn.getPointsChange());
        vo.setRelatedOrderId(txn.getRelatedOrderId());
        vo.setGmtCreate(txn.getGmtCreate());
        return vo;
    }
}
