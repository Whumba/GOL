public class Main{

	public static void main(String[] args){
		if(args.length != 0){
			ParameterData parameterData = Parameter.createParameterData(args);

			GameData gameData = Game.run(parameterData);

		} else System.out.println("No Parameters given!");
	}
}