package common;

/**
 * Created by bider_000 on 02.11.2015.
 */

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

public interface Helpers {

    static MonadicBinding<Number> divide(MonadicBinding<? extends Number> a, MonadicBinding<? extends Number> b) {
        return EasyBind.combine(a, b, (av, bv) ->
                av == null || bv == null ? Double.NaN :
                        av.doubleValue() / bv.doubleValue());
    }
    public static double clamp(double value, double min, double max) {
        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }
}

