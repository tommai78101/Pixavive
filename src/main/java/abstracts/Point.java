package abstracts;

public abstract class Point {
	public double x;
	public double y;

	public double getDistance(Point other) {
		return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
	}
}