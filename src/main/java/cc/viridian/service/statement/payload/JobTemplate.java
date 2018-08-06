package cc.viridian.service.statement.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobTemplate {
    private String account;
    private String currency;
    private String type;
    private String frequency;
    private String customerCode;
    private String recipient;
    private String formatAdapter;
    private String sendAdapter;
    private String coreBankAdapter;

}
