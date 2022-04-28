package exceptions;

public class InvalidNumberException extends NumberFormatException{
    public InvalidNumberException(String s){
        super(s);
    }
}
