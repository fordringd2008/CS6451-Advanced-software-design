package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.service.GuestPointsService;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Server-side JSP pages for guest points operations.
 *
 * @author Copilot
 * @since 1.0
 */
@Controller
@RequestMapping("/guest")
public class GuestJspController {

    private final GuestPointsService guestPointsService;

    /**
     * Creates the guest JSP controller.
     *
     * @param guestPointsService guest points application service
     */
    public GuestJspController(GuestPointsService guestPointsService) {
        this.guestPointsService = guestPointsService;
    }

    /**
     * IDOR defense: URL uid must match the session identity.
     * <p>
     * The URL uid is for routing only; access control uses the
     * server-side SecurityContext (OWASP: never trust
     * client-supplied identifiers).
     *
     * @param pathUid uid from the URL path
     * @throws AccessDeniedException if the uid does not match
     */
    private void checkOwnership(String pathUid) {
        String sessionUid = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        if (!pathUid.equals(sessionUid)) {
            throw new AccessDeniedException("Not your resource");
        }
    }

    /**
     * Renders the server-side points mall page.
     *
     * @param uid guest business key
     * @param model MVC model
     * @return points mall JSP view
     */
    @GetMapping("/{uid}/points-mall")
    public String pointsMall(@PathVariable String uid, Model model) {
        checkOwnership(uid);
        model.addAttribute("uid", uid);
        model.addAttribute("mall", guestPointsService.getPointsMall(uid));
        return "guest_points_mall";
    }

    /**
     * Renders the server-side my-points page.
     *
     * @param uid guest business key
     * @param model MVC model
     * @return my points JSP view
     */
    @GetMapping("/{uid}/my-points")
    public String myPoints(@PathVariable String uid, Model model) {
        checkOwnership(uid);
        model.addAttribute("uid", uid);
        model.addAttribute("points", guestPointsService.getMyPoints(uid));
        return "guest_my_points";
    }

    /**
     * Renders the server-side payment confirmation page.
     *
     * @param uid guest business key
     * @param pid product business key
     * @param model MVC model
     * @return payment confirmation JSP view
     */
    @GetMapping("/{uid}/confirm-payment/{pid}")
    public String confirmPayment(@PathVariable String uid, @PathVariable String pid, Model model) {
        checkOwnership(uid);
        model.addAttribute("uid", uid);
        model.addAttribute("payment", guestPointsService.getConfirmPayment(uid, pid));
        return "guest_confirm_payment";
    }

    /**
     * Processes the payment confirmation form and redirects to the points page.
     * <p>
     * Bean Validation failures and business errors (out of stock, insufficient
     * points, optimistic-lock conflict) are caught here and shown to the guest
     * as a flash message — {@code @RestControllerAdvice} does not cover
     * {@code @Controller} view methods, so without this catch the guest would
     * see a raw Whitelabel error page.
     *
     * @param uid guest business key
     * @param request submitted redemption form
     * @param bindingResult validation outcome of the form binding
     * @param redirectAttributes redirect message container
     * @return redirect to my-points on success, back to points-mall on error
     */
    @PostMapping("/{uid}/redemptions")
    public String redeem(@PathVariable String uid, @Valid @ModelAttribute RedemptionRequest request,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        checkOwnership(uid);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    bindingResult.getFieldErrors().stream()
                            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                            .reduce((a, b) -> a + "; " + b).orElse("invalid form data"));
            return "redirect:/guest/" + uid + "/points-mall";
        }
        request.setUid(uid);
        try {
            String oid = guestPointsService.redeem(request);
            redirectAttributes.addFlashAttribute("successMessage", "Payment confirmed. Order " + oid + " is paid.");
            return "redirect:/guest/" + uid + "/my-points";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/guest/" + uid + "/points-mall";
        }
    }
}
