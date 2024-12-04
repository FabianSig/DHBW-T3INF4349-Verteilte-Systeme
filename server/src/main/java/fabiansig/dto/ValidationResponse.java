package fabiansig.dto;

import java.util.List;

public record ValidationResponse(List<Result> success){
    public record Result(String label, double score) {}
}
