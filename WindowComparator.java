import java.util.Comparator;

public class WindowComparator implements Comparator<Window>{
	public int compare(Window w1, Window w2){
		if(w1.MinimalDSquare()<w2.MinimalDSquare()) return -1;
		return 1;
	}
}
