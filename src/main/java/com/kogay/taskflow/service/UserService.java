package com.kogay.taskflow.service;

import com.kogay.taskflow.dto.CustomUserDetails;
import com.kogay.taskflow.dto.UserEditDto;
import com.kogay.taskflow.dto.UserFilter;
import com.kogay.taskflow.dto.UserReadDto;
import com.kogay.taskflow.repository.UserRepository;
import com.kogay.taskflow.util.QPredicates;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static com.kogay.taskflow.entity.QUser.user;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Optional<UserReadDto> findById(Integer id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserReadDto.class));
    }

    public Page<UserReadDto> findAll(UserFilter userFilter,
                                     Pageable pageable) {
        Predicate predicate = QPredicates.builder()
                .add(userFilter.firstName(), user.firstName::likeIgnoreCase)
                .add(userFilter.lastName(), user.lastName::likeIgnoreCase)
                .add(userFilter.birthDate(), date -> user.birthDate.before(date).or(user.birthDate.eq(date)))
                .buildAnd();

        return userRepository.findAll(predicate, pageable)
                .map(user -> modelMapper.map(user, UserReadDto.class));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or principal.id == #id")
    public Optional<UserReadDto> update(Integer id, UserEditDto userEditDto) {
        return userRepository.findById(id)
                .map(user -> {
                    modelMapper.map(userEditDto, user);
                    return user;
                })
                .map(userRepository::saveAndFlush)
                .map(user -> modelMapper.map(user, UserReadDto.class));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public boolean delete(Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), Collections.singleton(user.getRole())))
                .orElseThrow(() -> new UsernameNotFoundException("failed to retrieve user: " + username));
    }
}
