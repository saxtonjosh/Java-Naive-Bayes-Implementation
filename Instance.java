import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Instance {

		private int category;
		private List<Double> atts;
		private int predictedCategory;

		public Instance(Scanner s){
			atts = new ArrayList<Double>();
			while (s.hasNextDouble()) atts.add(s.nextDouble());
			category = atts.get(atts.size()-1).intValue();
		}

		public Double getAtt(int index){
			return atts.get(index);
		}

		public int getCategory(){
			return category;
		}
		
		public void setPredictedCategory(int category) {
			this.predictedCategory = category;
		}
		
		public int getPredictedCategory() {
			return predictedCategory;
		}

		public String toString(){
			
			StringBuilder ans = new StringBuilder();
			for (int i = 0; i < atts.size() - 1; i++) {
				ans.append(" " + atts.get(i));
			}
			ans.append(" Actual category: " + atts.get(atts.size() - 1).intValue() + " Predicted category: " + predictedCategory);
			return ans.toString();
		}
}
