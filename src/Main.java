import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	@SuppressWarnings("resource")
	public static Graph readGraphFromFile() throws FileNotFoundException {

		Scanner sc = new Scanner(System.in);
		File file = null;
		int selected = sc.nextInt();
		switch (selected) {
		case 1:
			file = new File("ftv47.atsp");
			break;
		case 2:
			file = new File("ftv170.atsp");
			break;
		case 3:
			file = new File("rbg403.atsp");
			break;
		default:
			break;
		}
		Scanner scanner = new Scanner(file);

		for (int i = 0; i < 3; i++) {
			if (scanner.hasNextLine()) {
				scanner.nextLine();
			}
		}

		if (scanner.hasNext()) {
			scanner.next();
		}

		int dimension = scanner.nextInt();
		for (int i = 0; i < 4; i++) {
			if (scanner.hasNextLine()) {
				scanner.nextLine();
			}
		}

		Graph graph = new Graph(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				graph.addEdge(i, j, scanner.nextInt());
			}
		}
		graph.displayGraph();
		return graph;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {

		
		int selection;
		Graph graph = null;
		do {
			System.out.println("1. Wczytanie danych z pliku i wyœwietlenie wczytanych danych");
			System.out.println("2. Wprowadzenie kryterium stopu");
			System.out.println("3. Ustawienie wielkoœci populacji pocz¹tkowej");
			System.out.println("4. Ustawienie wspolczynnika mutacji");
			System.out.println("5. Ustawienie wspolczynnika krzyzowania");
			System.out.println("6. Wybor metody mutacji");
			System.out.println("7. Uruchomienie algorytmu dla wczytanych danych i ustawionych parametrow i wyswietlenie wynikow");
			System.out.println("Aby zakonczyc - 0");
			System.out.println("Wprowadz liczbê: ");
			Scanner sc = new Scanner(System.in);
			selection = sc.nextInt();
			switch (selection) {

			// Wczytanie danych z pliku
			case 1: {
				System.out.println("Wybierz plik, z ktorego wczytaæ dane: ");
				System.out.println("1. ftv47.atsp");
				System.out.println("2. ftv170.atsp");
				System.out.println("3. rbg403.atsp");
				graph = readGraphFromFile();
				// graph.displayGraph();

			}
				break;

			case 2: {

				System.out.println("Wprowadz kryterium stopu w sekundach: ");
				int maxTime;
				maxTime = sc.nextInt();
				GeneticAlgorithm.maxTime = maxTime;

			}
				break;

			case 3: {
				System.out.println("Wprowadz wielkosc populacji poczatkowej: ");
				int populationSize;
				populationSize = sc.nextInt();
				GeneticAlgorithm.populationSize = populationSize;

			}
				break;

			case 4: {
				Scanner scanner = new Scanner(System.in);
				System.out.println("Wprowadz wspolczynnik mutacji: ");
				float mutationProbability;
				mutationProbability = scanner.nextFloat();
				GeneticAlgorithm.mutationProbability = mutationProbability;
			}
				break;

			case 5: {
				System.out.println("Wprowadz wspolczynik krzyzowania: ");
				float crossoverProbabillity;
				crossoverProbabillity = sc.nextFloat();
				GeneticAlgorithm.crossoverProbability = crossoverProbabillity;
			}
				break;
			
			case 6:{
				System.out.println("Wybierz metode mutacji: ");
				System.out.println("1.Transpozycja");
				System.out.println("2.Insercja");
				int mutationMethod;
				mutationMethod = sc.nextInt();
				GeneticAlgorithm.mutationMethod = mutationMethod;
			}
			break;
				
			case 7: {
				GeneticAlgorithm.graph = graph;
				GeneticAlgorithm.geneticAlgorithm();
			}
				break;

			case 0: {
			}
				break;
			default: {
				System.out.println("Nieprawidlowy wybor");
			}
			}
		} while (selection != 0);

	}

}
