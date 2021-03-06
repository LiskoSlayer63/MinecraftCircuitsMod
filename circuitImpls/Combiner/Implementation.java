import java.io.Serializable;
//Convention: Combiners are named after their input widths
public class Implementation implements Serializable {

        long output;
        int bitWidth;
	
	public Implementation() {
	}

        public String config(int bitWidth) {
            if (!Utils.isValidBusWidth(bitWidth)) {
                return null;
            }
            this.bitWidth = bitWidth;
            return "" + bitWidth;
        }
	
	public void tick(long high, long low) {
            this.output = (high << bitWidth) + low;
	}

        public long value0() {
            return output;
        }

        public int[] outputWidths() {
            return new int[]{bitWidth * 2};
        }

        public int[] inputWidths() {
            return new int[]{bitWidth, bitWidth};
        }
}
