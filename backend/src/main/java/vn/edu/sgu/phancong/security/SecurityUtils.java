package vn.edu.sgu.phancong.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.edu.sgu.phancong.exception.ForbiddenException;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ForbiddenException("Chưa đăng nhập");
        }
        return principal;
    }

    public static Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static Integer getCurrentGiangVienId() {
        Integer gvId = getCurrentUser().getGiangVienId();
        if (gvId == null) {
            throw new ForbiddenException("Tài khoản không gắn với giảng viên");
        }
        return gvId;
    }

    public static Integer getCurrentBoMonId() {
        return getCurrentUser().getBoMonId();
    }

    public static boolean hasRole(String role) {
        return getCurrentUser().getVaiTro().equals(role);
    }
}
