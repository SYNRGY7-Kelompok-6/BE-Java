package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.entity.SavedAccounts;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.model.savedAccounts.SavedAccountsRequest;
import com.kelp_6.banking_apps.model.savedAccounts.SavedAccountsResponse;
import com.kelp_6.banking_apps.model.savedAccounts.UpdateSavedAccountRequest;
import com.kelp_6.banking_apps.repository.AccountRepository;
import com.kelp_6.banking_apps.repository.SavedAccountsRespository;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedAccountsServiceImpl implements SavedAccountsService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final SavedAccountsRespository savedAccountsRespository;

    @Override
    @Transactional
    public SavedAccountsResponse addSavedAccount(SavedAccountsRequest request) {
        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        Account beneficiaryAccount = accountRepository.findByAccountNumber(request.getBeneficiaryAccountNumber()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number not found"));

        if(Objects.equals(user.getAccount().getAccountNumber(), beneficiaryAccount.getAccountNumber())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't add your own account to saved list");

        SavedAccounts savedAccounts = new SavedAccounts();
        savedAccounts.setAccount(beneficiaryAccount);
        savedAccounts.setUser(user);
        savedAccounts.setFavorite(false);

        savedAccountsRespository.save(savedAccounts);

        return SavedAccountsResponse.builder()
                .savedBeneficiaryId(savedAccounts.getId().toString())
                .beneficiaryAccountNumber(savedAccounts.getAccount().getAccountNumber())
                .beneficiaryAccountName(savedAccounts.getAccount().getUser().getName())
                .favorite(savedAccounts.getFavorite())
                .build();
    }

    @Override
    public List<SavedAccountsResponse> getAllSavedAccounts(SavedAccountsRequest request) {
        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        List<SavedAccounts> savedAccountByUserId =
                request.getIsFavorite()
                ? savedAccountsRespository.findAllByUserIdAndLikePatternAndFavorite(user.getId(), request.getBeneficiaryName(), true)
                        : savedAccountsRespository.findAllByUserIdAndLikePattern(user.getId(), request.getBeneficiaryName());

        return savedAccountByUserId.stream().map(savedAccounts -> SavedAccountsResponse.builder()
                .savedBeneficiaryId(savedAccounts.getId().toString())
                .beneficiaryAccountNumber(savedAccounts.getAccount().getAccountNumber())
                .beneficiaryAccountName(savedAccounts.getAccount().getUser().getName())
                .favorite(savedAccounts.getFavorite())
                .build()).toList();
    }

    @Override
    public SavedAccountsResponse getSavedAccount(SavedAccountsRequest request) {
        UUID savedBeneficiaryId = UuidUtil.convertStringIntoUUID(request.getSavedBeneficiaryId());

        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        SavedAccounts savedAccount = savedAccountsRespository.findByIdAndUser_Id(user.getId(), savedBeneficiaryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "beneficiary account not found"));

        return SavedAccountsResponse.builder()
                .savedBeneficiaryId(savedAccount.getId().toString())
                .beneficiaryAccountName(savedAccount.getAccount().getUser().getName())
                .beneficiaryAccountNumber(savedAccount.getAccount().getAccountNumber())
                .favorite(savedAccount.getFavorite())
                .build();
    }

    @Override
    public SavedAccountsResponse updateSavedAccount(UpdateSavedAccountRequest request) {
        UUID savedBeneficiaryId = UuidUtil.convertStringIntoUUID(request.getSavedBeneficiaryId());

        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        SavedAccounts savedAccount = savedAccountsRespository.findByIdAndUser_Id(user.getId(), savedBeneficiaryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "beneficiary account not found"));

        savedAccount.setFavorite(request.getIsFavorite());
        savedAccountsRespository.save(savedAccount);

        return SavedAccountsResponse.builder()
                .savedBeneficiaryId(savedAccount.getId().toString())
                .beneficiaryAccountName(savedAccount.getAccount().getUser().getName())
                .beneficiaryAccountNumber(savedAccount.getAccount().getAccountNumber())
                .favorite(savedAccount.getFavorite())
                .build();
    }
}
