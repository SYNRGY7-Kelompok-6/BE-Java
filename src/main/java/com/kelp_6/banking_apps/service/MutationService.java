package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.mutation.MutationRequest;
import com.kelp_6.banking_apps.model.mutation.MutationResponse;

public interface MutationService {
    MutationResponse getMutation(MutationRequest request);
}
