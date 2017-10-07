package libai.classifiers.trees;

import libai.classifiers.dataset.DataSet;
import libai.classifiers.dataset.TextFileDataSet;
import java.io.File;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by kronenthaler on 30/09/2017.
 */
public class C45Test {
	private DataSet irisDataSet = new TextFileDataSet(new File("src/test/resources/iris.data"),
												new String[]{"sepal_length","sepal_width","petal_length","petal_width","class"},
												4);

	@Test
	public void loadTest() throws Exception {
		C45 tree = C45.getInstance(irisDataSet);

		String output = tree.toString();
		assertEquals(output.trim(), "[petal_width <= 1.35 {} be: 0.0]\n" +
				"\t[petal_length <= 1.65 {} be: 0.0]\n" +
				"\t\t[[class]=Iris-setosa {} e: 0.0]\n" +
				"\t[petal_length > 1.65 {} be: 0.0]\n" +
				"\t\t[sepal_width <= 2.75 {} be: 0.0]\n" +
				"\t\t\t[[class]=Iris-versicolor {} e: 0.0]\n" +
				"\t\t[sepal_width > 2.75 {} be: 0.0]\n" +
				"\t\t\t[sepal_width <= 2.95 {} be: 0.0]\n" +
				"\t\t\t\t[[class]=Iris-versicolor {} e: 0.0]\n" +
				"\t\t\t[sepal_width > 2.95 {} be: 0.0]\n" +
				"\t\t\t\t[sepal_length <= 5.5 {} be: 0.0]\n" +
				"\t\t\t\t\t[[class]=Iris-setosa {} e: 0.0]\n" +
				"\t\t\t\t[sepal_length > 5.5 {} be: 0.0]\n" +
				"\t\t\t\t\t[sepal_width <= 3.4 {} be: 0.0]\n" +
				"\t\t\t\t\t\t[[class]=Iris-versicolor {} e: 0.0]\n" +
				"\t\t\t\t\t[sepal_width > 3.4 {} be: 0.0]\n" +
				"\t\t\t\t\t\t[[class]=Iris-setosa {} e: 0.0]\n" +
				"[petal_width > 1.35 {} be: 0.0]\n" +
				"\t[petal_length <= 5.15 {} be: 0.0]\n" +
				"\t\t[petal_width <= 1.55 {} be: 0.0]\n" +
				"\t\t\t[petal_width <= 1.5 {} be: 0.0]\n" +
				"\t\t\t\t[petal_width <= 1.5 {} be: 0.0]\n" +
				"\t\t\t\t\t[[class]=Iris-versicolor {} e: 0.0]\n" +
				"\t\t\t\t[petal_width > 1.5 {} be: 0.0]\n" +
				"\t\t\t\t\t[[class]=Iris-virginica {} e: 0.0]\n" +
				"\t\t\t[petal_width > 1.5 {} be: 0.0]\n" +
				"\t\t\t\t[[class]=Iris-virginica {} e: 0.0]\n" +
				"\t\t[petal_width > 1.55 {} be: 0.0]\n" +
				"\t\t\t[petal_width <= 1.85 {} be: 0.0]\n" +
				"\t\t\t\t[sepal_width <= 3.1 {} be: 0.0]\n" +
				"\t\t\t\t\t[petal_width <= 1.8 {} be: 0.0]\n" +
				"\t\t\t\t\t\t[petal_width <= 1.75 {} be: 0.0]\n" +
				"\t\t\t\t\t\t\t[sepal_length <= 5.45 {} be: 0.0]\n" +
				"\t\t\t\t\t\t\t\t[[class]=Iris-virginica {} e: 0.0]\n" +
				"\t\t\t\t\t\t\t[sepal_length > 5.45 {} be: 0.0]\n" +
				"\t\t\t\t\t\t\t\t[[class]=Iris-versicolor {} e: 0.0]\n" +
				"\t\t\t\t\t\t[petal_width > 1.75 {} be: 0.0]\n" +
				"\t\t\t\t\t\t\t[[class]=Iris-virginica {} e: 0.0]\n" +
				"\t\t\t\t\t[petal_width > 1.8 {} be: 0.0]\n" +
				"\t\t\t\t\t\t[[class]=Iris-virginica {} e: 0.0]\n" +
				"\t\t\t\t[sepal_width > 3.1 {} be: 0.0]\n" +
				"\t\t\t\t\t[[class]=Iris-versicolor {} e: 0.0]\n" +
				"\t\t\t[petal_width > 1.85 {} be: 0.0]\n" +
				"\t\t\t\t[[class]=Iris-virginica {} e: 0.0]\n" +
				"\t[petal_length > 5.15 {} be: 0.0]\n" +
				"\t\t[[class]=Iris-virginica {} e: 0.0]");
	}

	@Test
	public void loadAndQuilanPruneTest() throws Exception {
		C45 tree = C45.getInstancePrune(irisDataSet, C45.PruneType.QUINLANS_PRUNE);

		String output = tree.toString();
		assertEquals(output.trim(), "[petal_width <= 1.35 {[class]=Iris-versicolor=50, [class]=Iris-virginica=50, [class]=Iris-setosa=50} be: NaN]\n" +
				"\t[petal_length <= 1.65 {[class]=Iris-versicolor=28, [class]=Iris-virginica=0, [class]=Iris-setosa=50} be: 0.9999999999999998]\n" +
				"\t\t[[class]=Iris-setosa {[class]=Iris-versicolor=0, [class]=Iris-virginica=0, [class]=Iris-setosa=44} e: 0.9999999999999998]\n" +
				"\t[petal_length > 1.65 {[class]=Iris-versicolor=28, [class]=Iris-virginica=0, [class]=Iris-setosa=50} be: 0.9999999999999998]\n" +
				"\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=28, [class]=Iris-virginica=0, [class]=Iris-setosa=6} e: 0.9999999999999998]\n" +
				"[petal_width > 1.35 {[class]=Iris-versicolor=50, [class]=Iris-virginica=50, [class]=Iris-setosa=50} be: NaN]\n" +
				"\t[petal_length <= 5.15 {[class]=Iris-versicolor=22, [class]=Iris-virginica=50, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t[petal_width <= 1.55 {[class]=Iris-versicolor=22, [class]=Iris-virginica=16, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t[petal_width <= 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[petal_width <= 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} e: 0.9335830095718656]\n" +
				"\t\t\t\t[petal_width > 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[[class]=Iris-virginica {} e: NaN]\n" +
				"\t\t\t[petal_width > 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[[class]=Iris-virginica {} e: NaN]\n" +
				"\t\t[petal_width > 1.55 {[class]=Iris-versicolor=22, [class]=Iris-virginica=16, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t[petal_width <= 1.85 {[class]=Iris-versicolor=5, [class]=Iris-virginica=14, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[sepal_width <= 3.1 {[class]=Iris-versicolor=5, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[petal_width <= 1.8 {[class]=Iris-versicolor=2, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t\t[[class]=Iris-virginica {[class]=Iris-versicolor=2, [class]=Iris-virginica=6, [class]=Iris-setosa=0} e: 0.9999999999999998]\n" +
				"\t\t\t\t\t[petal_width > 1.8 {[class]=Iris-versicolor=2, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t\t[[class]=Iris-virginica {} e: NaN]\n" +
				"\t\t\t\t[sepal_width > 3.1 {[class]=Iris-versicolor=5, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=3, [class]=Iris-virginica=0, [class]=Iris-setosa=0} e: 1.0000000000000002]\n" +
				"\t\t\t[petal_width > 1.85 {[class]=Iris-versicolor=5, [class]=Iris-virginica=14, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[[class]=Iris-virginica {[class]=Iris-versicolor=0, [class]=Iris-virginica=8, [class]=Iris-setosa=0} e: 0.9999999999999998]\n" +
				"\t[petal_length > 5.15 {[class]=Iris-versicolor=22, [class]=Iris-virginica=50, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t[[class]=Iris-virginica {[class]=Iris-versicolor=0, [class]=Iris-virginica=34, [class]=Iris-setosa=0} e: 0.9999999999999998]");
	}

	@Test
	public void loadAndLaplacePruneTest() throws Exception {
		C45 tree = C45.getInstancePrune(irisDataSet, C45.PruneType.LAPLACE_PRUNE);

		String output = tree.toString();
		assertEquals(output.trim(), "[petal_width <= 1.35 {[class]=Iris-versicolor=50, [class]=Iris-virginica=50, [class]=Iris-setosa=50} be: NaN]\n" +
				"\t[petal_length <= 1.65 {[class]=Iris-versicolor=28, [class]=Iris-virginica=0, [class]=Iris-setosa=50} be: 0.09772231314784506]\n" +
				"\t\t[[class]=Iris-setosa {[class]=Iris-versicolor=0, [class]=Iris-virginica=0, [class]=Iris-setosa=44} e: 0.0425531914893617]\n" +
				"\t[petal_length > 1.65 {[class]=Iris-versicolor=28, [class]=Iris-virginica=0, [class]=Iris-setosa=50} be: 0.09772231314784506]\n" +
				"\t\t[sepal_width <= 2.75 {[class]=Iris-versicolor=28, [class]=Iris-virginica=0, [class]=Iris-setosa=6} be: 0.16911764705882354]\n" +
				"\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=17, [class]=Iris-virginica=0, [class]=Iris-setosa=0} e: 0.1]\n" +
				"\t\t[sepal_width > 2.75 {[class]=Iris-versicolor=28, [class]=Iris-virginica=0, [class]=Iris-setosa=6} be: 0.16911764705882354]\n" +
				"\t\t\t[sepal_width <= 2.95 {[class]=Iris-versicolor=11, [class]=Iris-virginica=0, [class]=Iris-setosa=6} be: 0.23823529411764705]\n" +
				"\t\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=9, [class]=Iris-virginica=0, [class]=Iris-setosa=0} e: 0.16666666666666666]\n" +
				"\t\t\t[sepal_width > 2.95 {[class]=Iris-versicolor=11, [class]=Iris-virginica=0, [class]=Iris-setosa=6} be: 0.23823529411764705]\n" +
				"\t\t\t\t[sepal_length <= 5.5 {[class]=Iris-versicolor=2, [class]=Iris-virginica=0, [class]=Iris-setosa=6} be: 0.31875]\n" +
				"\t\t\t\t\t[[class]=Iris-setosa {[class]=Iris-versicolor=0, [class]=Iris-virginica=0, [class]=Iris-setosa=5} e: 0.25]\n" +
				"\t\t\t\t[sepal_length > 5.5 {[class]=Iris-versicolor=2, [class]=Iris-virginica=0, [class]=Iris-setosa=6} be: 0.31875]\n" +
				"\t\t\t\t\t[sepal_width <= 3.4 {[class]=Iris-versicolor=2, [class]=Iris-virginica=0, [class]=Iris-setosa=1} be: 0.43333333333333335]\n" +
				"\t\t\t\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=2, [class]=Iris-virginica=0, [class]=Iris-setosa=0} e: 0.4]\n" +
				"\t\t\t\t\t[sepal_width > 3.4 {[class]=Iris-versicolor=2, [class]=Iris-virginica=0, [class]=Iris-setosa=1} be: 0.43333333333333335]\n" +
				"\t\t\t\t\t\t[[class]=Iris-setosa {[class]=Iris-versicolor=0, [class]=Iris-virginica=0, [class]=Iris-setosa=1} e: 0.5]\n" +
				"[petal_width > 1.35 {[class]=Iris-versicolor=50, [class]=Iris-virginica=50, [class]=Iris-setosa=50} be: NaN]\n" +
				"\t[petal_length <= 5.15 {[class]=Iris-versicolor=22, [class]=Iris-virginica=50, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t[petal_width <= 1.55 {[class]=Iris-versicolor=22, [class]=Iris-virginica=16, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t[petal_width <= 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[petal_width <= 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} e: 0.18181818181818182]\n" +
				"\t\t\t\t[petal_width > 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[[class]=Iris-virginica {} e: Infinity]\n" +
				"\t\t\t[petal_width > 1.5 {[class]=Iris-versicolor=17, [class]=Iris-virginica=2, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[[class]=Iris-virginica {} e: Infinity]\n" +
				"\t\t[petal_width > 1.55 {[class]=Iris-versicolor=22, [class]=Iris-virginica=16, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t[petal_width <= 1.85 {[class]=Iris-versicolor=5, [class]=Iris-virginica=14, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[sepal_width <= 3.1 {[class]=Iris-versicolor=5, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[petal_width <= 1.8 {[class]=Iris-versicolor=2, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t\t[petal_width <= 1.75 {[class]=Iris-versicolor=2, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: 0.31875]\n" +
				"\t\t\t\t\t\t\t[sepal_length <= 5.45 {[class]=Iris-versicolor=2, [class]=Iris-virginica=1, [class]=Iris-setosa=0} be: 0.43333333333333335]\n" +
				"\t\t\t\t\t\t\t\t[[class]=Iris-virginica {[class]=Iris-versicolor=0, [class]=Iris-virginica=1, [class]=Iris-setosa=0} e: 0.5]\n" +
				"\t\t\t\t\t\t\t[sepal_length > 5.45 {[class]=Iris-versicolor=2, [class]=Iris-virginica=1, [class]=Iris-setosa=0} be: 0.43333333333333335]\n" +
				"\t\t\t\t\t\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=2, [class]=Iris-virginica=0, [class]=Iris-setosa=0} e: 0.4]\n" +
				"\t\t\t\t\t\t[petal_width > 1.75 {[class]=Iris-versicolor=2, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: 0.31875]\n" +
				"\t\t\t\t\t\t\t[[class]=Iris-virginica {[class]=Iris-versicolor=0, [class]=Iris-virginica=5, [class]=Iris-setosa=0} e: 0.25]\n" +
				"\t\t\t\t\t[petal_width > 1.8 {[class]=Iris-versicolor=2, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t\t[[class]=Iris-virginica {} e: Infinity]\n" +
				"\t\t\t\t[sepal_width > 3.1 {[class]=Iris-versicolor=5, [class]=Iris-virginica=6, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t\t[[class]=Iris-versicolor {[class]=Iris-versicolor=3, [class]=Iris-virginica=0, [class]=Iris-setosa=0} e: 0.3333333333333333]\n" +
				"\t\t\t[petal_width > 1.85 {[class]=Iris-versicolor=5, [class]=Iris-virginica=14, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t\t\t[[class]=Iris-virginica {[class]=Iris-versicolor=0, [class]=Iris-virginica=8, [class]=Iris-setosa=0} e: 0.18181818181818182]\n" +
				"\t[petal_length > 5.15 {[class]=Iris-versicolor=22, [class]=Iris-virginica=50, [class]=Iris-setosa=0} be: NaN]\n" +
				"\t\t[[class]=Iris-virginica {[class]=Iris-versicolor=0, [class]=Iris-virginica=34, [class]=Iris-setosa=0} e: 0.05405405405405406]");
	}
}
