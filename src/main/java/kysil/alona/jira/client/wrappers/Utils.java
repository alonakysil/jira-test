package kysil.alona.jira.client.wrappers;

import java.util.List;
import java.util.Scanner;

public class Utils {
	public static Object collectMenuInput(String question, List<MenuItemWrapper> items) {		
		System.out.println(question);
	    int i = 1;
        for (MenuItemWrapper item : items) {
			System.out.println(i + " - " + item.getName());
	    	i++;
        }		
        System.out.println();
	    @SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
	    int enteredNumber;		
        do {
        	System.out.println("Please enter number from listed above!\n");
	        while (!sc.hasNextInt()) {
	        	System.out.println("Please enter number!\n");
	        	sc.next();
	        }
	        enteredNumber = sc.nextInt();
	        
	    } while (enteredNumber < 1 || enteredNumber > items.size());
	    System.out.println(enteredNumber);
		return items.get(enteredNumber - 1).getId();        
	}

}
