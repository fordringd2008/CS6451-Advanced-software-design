package com.hotel.hotel_loyalty.service;

import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.vo.ConfirmPaymentVO;
import com.hotel.hotel_loyalty.vo.AccountVO;
import com.hotel.hotel_loyalty.vo.MyPointsVO;
import com.hotel.hotel_loyalty.vo.PointsMallVO;

/**
 * Guest-facing points mall, points profile and redemption operations.
 *
 * @author Copilot
 * @since 1.0
 */
public interface GuestPointsService {

    /**
     * Loads the authenticated guest's account details.
     *
     * @param uid guest business key
     * @return account details
     */
    AccountVO getAccount(String uid);

    /**
     * Loads the guest's points mall.
     *
     * @param uid guest business key
     * @return points balance and available products
     */
    PointsMallVO getPointsMall(String uid);

    /**
     * Loads the guest's points profile and redemption records.
     *
     * @param uid guest business key
     * @return points profile
     */
    MyPointsVO getMyPoints(String uid);

    /**
     * Loads product and payment information for confirmation.
     *
     * @param uid guest business key
     * @param pid product business key
     * @return confirmation-page data
     */
    ConfirmPaymentVO getConfirmPayment(String uid, String pid);

    /**
     * Completes a product redemption atomically.
     *
     * @param request redemption request
     * @return generated order business key
     * @throws IllegalArgumentException when a request or payment method is invalid
     * @throws IllegalStateException when stock or points changed during redemption
     */
    String redeem(RedemptionRequest request);
}
