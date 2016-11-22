public class Triple<A, B, C> {

    private A a;
    private B b;
    private C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public A first(){
    	return this.a;
    }
    
    public B second(){
    	return this.b;
    }
    
    public C third(){
    	return this.c;
    }
    
    public void UpdateB(B b1){
    	this.b=b1;
    }
    
    public void Updatec(C c1){
    	this.c=c1;
    }
    
    public void UpdateA(A a1){
    	this.a=a1;
    }

}