package defacement.view;

import defacement.model.IndicatorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ActiveIndicatorView {

    private Long id;
    private IndicatorType type;
    private String value;
    private String filename;
    private String hashValue;
    private String description;
    private String createdBy;
    private List<String> targetNames;
}