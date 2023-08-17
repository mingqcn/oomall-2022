package cn.edu.xmu.oomall.alipay.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DownloadUrlQueryRetVo {
    @JsonProperty("bill_download_url")
    private String billDownloadUrl;

    private String code;
    private String msg;

    public DownloadUrlQueryRetVo(String billDownloadUrl) {
        this.billDownloadUrl = billDownloadUrl;
        this.code = "10000";
        this.msg = "Success";
    }
}
