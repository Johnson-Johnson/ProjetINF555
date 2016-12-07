import java.util.Comparator;

public class WindowBinarySearch implements Comparator<Window>{
	public int compare(Window w1, Window w2){
		if(w1.Left()<w2.Right()) return -1;
		return 1;
	}

}
