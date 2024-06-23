package parser;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class Node {
    private String id;
    private String name;
    private int type;
}
