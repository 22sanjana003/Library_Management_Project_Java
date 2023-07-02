

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Main {
    public static void main(String[] args) {

        LocalDate d1=LocalDate.parse("2021-09-02");
        LocalDate d2=LocalDate.parse("2021-09-22");
        int daydif= (int)(ChronoUnit.DAYS.between(d1,d2));

        if(daydif>15)
        {
            int days=daydif-15;
            int fine=days*5;
            System.out.println(daydif+"  "+fine);
        }
        else
            System.out.println(daydif);
    }
    }

