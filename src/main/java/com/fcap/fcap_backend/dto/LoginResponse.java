package com.fcap.fcap_backend.dto;

public record LoginResponse(String token, String role, Long userId,
                            String lecturerId, String departmentId, String hoTen) {}