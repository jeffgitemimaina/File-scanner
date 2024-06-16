package com.velocistech.filescanner01.Registration;
import org.springframework.stereotype.Service;
import java.util.function.Predicate;

@Service
public class EmailValidator  implements Predicate<String> {

    @Override
    public boolean test(String s) {
     //   TODO: Regex validate email
        return true;
    }
}
