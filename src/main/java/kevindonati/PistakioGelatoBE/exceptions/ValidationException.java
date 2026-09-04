package kevindonati.PistakioGelatoBE.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private List<String> errorsList;

    public ValidationException(List<String> errorsList) {
        super("Validation errors");
        this.errorsList = errorsList;
    }

    public ValidationException(String message) {
        super(message);
    }
}
