public class Pair<A, B> {

    private A a;
    private B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
    
    public A first(){
    	return this.a;
    }
    
    public B second(){
    	return this.b;
    }
    
    public void UpdateB(B b1){
    	this.b=b1;
    }
    
    public void UpdateA(A a1){
    	this.a=a1;
    }

}