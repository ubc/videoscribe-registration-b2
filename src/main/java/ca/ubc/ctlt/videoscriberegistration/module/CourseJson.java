package ca.ubc.ctlt.videoscriberegistration.module;

// One limitation of using ngResource is that it expects every call to return
// an object, so bare strings or numbers or arrays will either behave
// unexpectedly or throw an exception. So we sometimes need to use
// intermediate objects to get the structure we want in the JSON.
class CourseJson implements Comparable<CourseJson>
{
	public String name;
	public String role;
	public boolean availability;

	@Override
	public int compareTo(CourseJson arg0)
	{
		return name.compareTo(arg0.name);
	}
}