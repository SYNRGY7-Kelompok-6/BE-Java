package com.kelp_6.banking_apps.controller.savedAccounts;

import com.kelp_6.banking_apps.model.savedAccounts.SavedAccountsRequest;
import com.kelp_6.banking_apps.model.savedAccounts.SavedAccountsResponse;
import com.kelp_6.banking_apps.model.savedAccounts.UpdateSavedAccountRequest;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.SavedAccountsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/saved-accounts")
public class SavedAccountController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SavedAccountController.class);
    private final SavedAccountsService savedAccountsService;

    @PostMapping(
            value = {"", "/"},
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SavedAccountsResponse> addSavedAccount(@RequestBody @Valid SavedAccountsRequest request, Authentication authentication){
        LOGGER.info("accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        request.setUserID(userDetails.getUsername());

        SavedAccountsResponse savedAccountsResponse = savedAccountsService.addSavedAccount(request);

        return WebResponse.<SavedAccountsResponse>builder()
                .status("success")
                .message("account has been saved successfully")
                .data(savedAccountsResponse)
                .build();
    }

    @GetMapping(
            value = {"", "/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SavedAccountsResponse>> getAllSavedAccounts(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "isFavorite", required = false, defaultValue = "false") Boolean isFavorite,
            Authentication authentication
            ){
        LOGGER.info("accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        SavedAccountsRequest request = SavedAccountsRequest.builder()
                .userID(userDetails.getUsername())
                .beneficiaryName(q)
                .isFavorite(isFavorite)
                .build();

        List<SavedAccountsResponse> allSavedAccounts = savedAccountsService.getAllSavedAccounts(request);

        return WebResponse.<List<SavedAccountsResponse>>builder()
                .status("success")
                .message("accounts has been retrieved successfully")
                .data(allSavedAccounts)
                .build();

    }

    @GetMapping(
            value = {"/{savedBeneficiaryId}", "/{savedBeneficiaryId}/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SavedAccountsResponse> getSavedAccountBySavedBeneficiaryId(
            @PathVariable(value = "savedBeneficiaryId") String savedBeneficiaryId,
            Authentication authentication
    ){
        LOGGER.info("accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        SavedAccountsRequest request = SavedAccountsRequest.builder()
                .savedBeneficiaryId(savedBeneficiaryId)
                .userID(userDetails.getUsername())
                .build();

        SavedAccountsResponse savedAccount = savedAccountsService.getSavedAccount(request);

        return WebResponse.<SavedAccountsResponse>builder()
                .status("success")
                .message("account has been retrieved successfully")
                .data(savedAccount)
                .build();
    }

    @PatchMapping(
            value = {"/{savedBeneficiaryId}", "/{savedBeneficiaryId}/"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SavedAccountsResponse> updateSavedAccount(
            @PathVariable(value = "savedBeneficiaryId") String savedBeneficiaryId,
            @RequestBody @Valid UpdateSavedAccountRequest request,
            Authentication authentication
    ){
        LOGGER.info("accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        request.setSavedBeneficiaryId(savedBeneficiaryId);
        request.setUserID(userDetails.getUsername());

        SavedAccountsResponse updatedSavedAccountsResponse = savedAccountsService.updateSavedAccount(request);

        return WebResponse.<SavedAccountsResponse>builder()
                .status("success")
                .message("account has been saved to favorite")
                .data(updatedSavedAccountsResponse)
                .build();
    }
}
