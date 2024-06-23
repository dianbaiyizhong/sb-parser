package parser;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@Builder
@EqualsAndHashCode(of = {"from", "to"})
public class Edge {
    private String from;


    private String to;
}
