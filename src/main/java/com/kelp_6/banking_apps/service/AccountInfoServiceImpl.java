package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.mapper.MutationResponseMapper;
import com.kelp_6.banking_apps.model.mutation.AccountInfoRequest;
import com.kelp_6.banking_apps.model.mutation.AccountInfoResponse;
import com.kelp_6.banking_apps.repository.AccountRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AccountInfoServiceImpl implements AccountInfoService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final MutationResponseMapper mutationResponseMapper;

    @Override
    public AccountInfoResponse getAccountInfo(AccountInfoRequest request) {
        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));
        Account account = accountRepository.findAccountByAccountNumberAndByUser_Username(user.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number doesn't exists"));

        return mutationResponseMapper.toDataDTO(account);
    }
}
