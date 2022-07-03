package com.venzyk;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/evening")
public class EveningServlet extends HttpServlet {

    Map<UUID, Map<String, String>> customSession = new ConcurrentHashMap<>();
    final static String CUSTOM_SESSION_ID = "custom_session_id";

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nameFromParameter = req.getParameter("name");
        UUID customSessionId = Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals(CUSTOM_SESSION_ID))
                .findAny()
                .map(cookie -> UUID.fromString(cookie.getValue()))
                .orElse(UUID.randomUUID());
        String name;
        if (nameFromParameter != null) {
            name = nameFromParameter;
            customSession.put(customSessionId, Map.of("name", name));
        } else if (customSession.get(customSessionId) != null &&
                (customSession.get(customSessionId).get("name") != null)) {
            name = String.valueOf(customSession.get(customSessionId).get("name"));
        } else {
            customSession.put(customSessionId, Map.of());
            name = "Buddy";
        }
        resp.addCookie(new Cookie(CUSTOM_SESSION_ID, customSessionId.toString()));

        resp.getWriter().println("Good evening, " + name);
    }
}
