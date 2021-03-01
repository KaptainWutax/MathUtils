package kaptainwutax.mathutils.component;

@FunctionalInterface
public interface Norm<C, R> {

	R get(C component);

}
