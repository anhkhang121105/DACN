package com.fcap.fcap_backend.error;

import java.util.List;

public record ApiError(int status, String code, String message, List<Object> details) {}