/**Class used to store windows with
 * @author etienne
 *
 * @param <A> b0 (like in the article)
 * @param <B> b1 "
 * @param <C> d0 "
 * @param <D> d1 "
 * @param <E> sigma "
 * @param <F> tau "
 * @param <G> vertex v1 to find the edge on which is the window
 * @param <H> vertex v2 "
 * @param <I> index of pseudosource
 */

public class Tuple<A, B, C, D, E, F> {
	
	public A a;
    public B b;
    public C c;
    public D d;
    public E e;
    public F f;

    public Tuple(A a, B b, C c, D d, E e, F f) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }
    public void UpdateA(A a1){
    	this.a=a1;
    }
    public void UpdateB(B b1){
    	this.b=b1;
    }
    public void UpdateC(C c1){
    	this.c=c1;
    }
    public void UpdateD(D d1){
    	this.d=d1;
    }
    public void UpdateE(E e1){
    	this.e=e1;
    }
    public void UpdateF(F f1){
    	this.f=f1;
    }
}
