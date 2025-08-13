package com.codewithiyke.dreamshops.security.user;

import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        Optional.ofNullable(userRepository.findByEmail(email))
            .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    return ShopUserDetails.buildUserDetails(user);
  }
}
