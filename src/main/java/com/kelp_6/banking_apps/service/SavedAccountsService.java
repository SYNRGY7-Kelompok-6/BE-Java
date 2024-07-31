package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.savedAccounts.SavedAccountsRequest;
import com.kelp_6.banking_apps.model.savedAccounts.SavedAccountsResponse;
import com.kelp_6.banking_apps.model.savedAccounts.UpdateSavedAccountRequest;

import java.util.List;

public interface SavedAccountsService {
    SavedAccountsResponse addSavedAccount(SavedAccountsRequest request);
    List<SavedAccountsResponse> getAllSavedAccounts(SavedAccountsRequest request);
    SavedAccountsResponse getSavedAccount(SavedAccountsRequest request);
    SavedAccountsResponse updateSavedAccount(UpdateSavedAccountRequest request);
}
