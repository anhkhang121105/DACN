package vn.edu.sgu.phancong.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LichRanhBanBulkRequest {

    private Integer hocKyId;
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private Integer thu;
        private Integer ca;
        private Boolean ranh = true;
    }
}
