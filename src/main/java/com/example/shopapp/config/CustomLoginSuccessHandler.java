package com.example.shopapp.config;

import com.example.shopapp.service.UserService;
import com.example.shopapp.controller.ShoppingCartController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ShoppingCartController shoppingCartController;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        String username = authentication.getName();

        if (session != null && username != null) {
            var user = userService.findByUsername(username);
            shoppingCartController.mergeSessionCartToDatabase(session, () -> username);
        }

        response.sendRedirect("/");
    }
}

