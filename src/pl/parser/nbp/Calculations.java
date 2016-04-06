package pl.parser.nbp;

import java.util.List;

/**
 * Created by Maciej Tomczyk.
 */
class Calculations {

    private Double calculateSum(List<Double> list) {
        Double buyPriceSum = 0.0;
        for (Double item : list) {
            buyPriceSum += item;
        }
        return buyPriceSum;
    }

    public Double calculateAvarage(List<Double> list) {
        Double sum = calculateSum(list);
        return sum / list.size();
    }

    private Double calculateVariance(List<Double> list) {
        Double mean = calculateAvarage(list);
        Double temp = 0.0;
        for (Double a : list)
            temp += (mean - a) * (mean - a);
        return temp / list.size();
    }

    public Double getStdDev(List<Double> list) {
        return Math.sqrt(calculateVariance(list));
    }
}

