package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.service.GuestPointsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
     * Renders the server-side points mall page.
     *
     * @param uid guest business key
     * @param model MVC model
     * @return points mall JSP view
     */
    @GetMapping("/{uid}/points-mall")
    public String pointsMall(@PathVariable String uid, Model model) {
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
        model.addAttribute("uid", uid);
        model.addAttribute("payment", guestPointsService.getConfirmPayment(uid, pid));
        return "guest_confirm_payment";
    }

    /**
     * Processes the payment confirmation form and redirects to the points page.
     *
     * @param uid guest business key
     * @param request submitted redemption form
     * @param redirectAttributes redirect message container
     * @return redirect to the guest points page
     */
    @PostMapping("/{uid}/redemptions")
    public String redeem(@PathVariable String uid, @ModelAttribute RedemptionRequest request,
                         RedirectAttributes redirectAttributes) {
        request.setUid(uid);
        String oid = guestPointsService.redeem(request);
        redirectAttributes.addFlashAttribute("successMessage", "Payment confirmed. Order " + oid + " is paid.");
        return "redirect:/guest/" + uid + "/my-points";
    }
}
