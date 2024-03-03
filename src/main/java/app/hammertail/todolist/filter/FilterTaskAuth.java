package app.hammertail.todolist.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import app.hammertail.todolist.user.IUserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException,
      IOException {

    var servletPath = request.getServletPath();

    if (servletPath.equals("/tasks/")) {
      String authorization = request.getHeader("Authorization");

      String authEncoded = authorization.substring("Basic".length()).trim();

      byte[] authDecoded = java.util.Base64.getDecoder().decode(authEncoded);

      String authString = new String(authDecoded);

      String[] credentials = authString.split(":");

      String username = credentials[0];
      String password = credentials[1];

      var user = this.userRepository.findByUsername(username);

      if (user == null) {
        response.sendError(401);
        return;
      } else {
        var passwordVeriry = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!passwordVeriry.verified) {
          response.sendError(401);
          return;
        }

        request.setAttribute("idUser", user.getId());

        filterChain.doFilter(request, response);
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
