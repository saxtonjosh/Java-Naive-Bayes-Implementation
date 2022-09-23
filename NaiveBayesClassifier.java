import java.io.File;
import java.io.IOException;
import java.util.*;

public class NaiveBayesClassifier {

	private int numCategories = 2;
	private int numAtts = 12;
	private List<String> categoryNames = new ArrayList<String>();
	private List<Double> attTrueSpamCounts = new ArrayList<Double>(Collections.nCopies(13, 1.0));
	private List<Double> attFalseSpamCounts = new ArrayList<Double>(Collections.nCopies(13, 1.0));
	private List<Double> attTrueNotSpamCounts = new ArrayList<Double>(Collections.nCopies(13, 1.0));
	private List<Double> attFalseNotSpamCounts = new ArrayList<Double>(Collections.nCopies(13, 1.0));
	
	private void Classify(String trainingSet, String testSet) {
		// Read instances into useable format
		List<Instance> trainingInstances = readDataFile(trainingSet);
		List<Instance> testInstances = readDataFile(testSet);
		
		// Count instances of features occuring in each class
		for (Instance instance : trainingInstances) {
			for (int i = 0; i < numAtts + 1 ; i++) {
				if (instance.getAtt(i) == 1.0 && instance.getCategory() == 1) {
					attTrueSpamCounts.set(i, attTrueSpamCounts.get(i) + 1);
				} else if (instance.getAtt(i) == 0.0 && instance.getCategory() == 1) {
					attFalseSpamCounts.set(i, attFalseSpamCounts.get(i) + 1);
				} else if (instance.getAtt(i) == 1.0 && instance.getCategory() == 0) {
					attTrueNotSpamCounts.set(i, attTrueNotSpamCounts.get(i) + 1);
				} else if (instance.getAtt(i) == 0.0 && instance.getCategory() == 0) {
					attFalseNotSpamCounts.set(i, attFalseNotSpamCounts.get(i) + 1);
				}
			}
		}
		
		// Calculate and store P(Feature | Class)
		for (int i = 0; i < numAtts ; i++) {
			Double totalAttSpam = attTrueSpamCounts.get(i) + attFalseSpamCounts.get(i);
			Double totalAttNotSpam = attTrueNotSpamCounts.get(i) + attFalseNotSpamCounts.get(i);
			attTrueSpamCounts.set(i, attTrueSpamCounts.get(i) / totalAttSpam);
			attFalseSpamCounts.set(i, attFalseSpamCounts.get(i) / totalAttSpam);
			attTrueNotSpamCounts.set(i, attTrueNotSpamCounts.get(i) / totalAttNotSpam);
			attFalseNotSpamCounts.set(i, attFalseNotSpamCounts.get(i) / totalAttNotSpam);
			System.out.println("P(F" + i + " = 0 | C = 0) = " + attFalseNotSpamCounts.get(i));
			System.out.println("P(F" + i + " = 0 | C = 1) = " + attFalseSpamCounts.get(i));
			System.out.println("P(F" + i + " = 1 | C = 0) = " + attTrueNotSpamCounts.get(i));
			System.out.println("P(F" + i + " = 1 | C = 1) = " + attTrueSpamCounts.get(i));
		}
		
		
		// Predict class labels for testSet
		Double probSpam = (attTrueSpamCounts.get(12) + attFalseSpamCounts.get(12)) / (Double.valueOf(trainingInstances.size() + 2.0));
		for (Instance instance : testInstances) {
			Double probSpamNumerator = probSpam;
			Double probNotSpamNumerator = 1.0 - probSpam;
			for (int i = 0; i < numAtts ; i++) {
				if (instance.getAtt(i) == 1.0) {
					probSpamNumerator = probSpamNumerator * attTrueSpamCounts.get(i);
					probNotSpamNumerator = probNotSpamNumerator * attTrueNotSpamCounts.get(i);
				} else if (instance.getAtt(i) == 0.0) {
					probSpamNumerator = probSpamNumerator * attFalseSpamCounts.get(i);
					probNotSpamNumerator = probNotSpamNumerator * attFalseNotSpamCounts.get(i);
				}
			}
			System.out.println("P(C = 0, F = Instance:" + testInstances.indexOf(instance) + ") = " + probNotSpamNumerator);
			System.out.println("P(C = 1, F = Instance:" + testInstances.indexOf(instance) + ") = " + probSpamNumerator);
			if (probSpamNumerator > probNotSpamNumerator) {
				instance.setPredictedCategory(1);
			} else if (probSpamNumerator < probNotSpamNumerator){
				instance.setPredictedCategory(0);
			} else {
				throw new RuntimeException("Something is fucked");
			}
			System.out.println("Predicted Category of Instance " + testInstances.indexOf(instance) + " = " + categoryNames.get(instance.getPredictedCategory()));
		}
	}
	
	
	private List<Instance> readInstances(Scanner din){
		/* instance = space separated attribute values and class number */
		List<Instance> instances = new ArrayList<Instance>();
		while (din.hasNext()){ 
			Scanner line = new Scanner(din.nextLine());
			instances.add(new Instance(line));
		}
		System.out.println("Read " + instances.size()+" instances");
		return instances;
	}
	
	private List<Instance> readDataFile(String fname){
		/* format of file:
		 * each line corresponds to 1 instance 
		 * each instance is a bunch of 1's or 0's corresponding to whether that feature is present or not
		 * separated by spaces, the last number on each line is the class label
		 */
		System.out.println("Reading data from file "+fname);
		try {
			Scanner din = new Scanner(new File(fname));
			categoryNames = new ArrayList<String>();
			categoryNames.add("Not Spam");
			categoryNames.add("Spam");
			System.out.println(numCategories +" categories");
			System.out.println(numAtts +" attributes");
			List<Instance> inputs = readInstances(din);
			din.close();
			return inputs;
		}
		catch (IOException e) {
			throw new RuntimeException("Data File caused IO exception");
		}
	}
	
	public static void main(String[] args) {
		NaiveBayesClassifier test = new NaiveBayesClassifier();
		test.Classify(args[0], args[1]);
	}
}