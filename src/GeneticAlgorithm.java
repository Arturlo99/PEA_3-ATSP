import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class GeneticAlgorithm {
	static Random random = new Random();
	static Graph graph;
	static int tournamentSize = 2;
	static int populationSize = 1000;
	static float mutationProbability = 0.01f;
	static float crossoverProbability = 0.8f;
	static int mutationMethod;
	static int maxTime = 30; // Kryterium stopu

	public static void geneticAlgorithm() {

		long startTime = System.nanoTime();
		int[] populationScore = new int[populationSize];
		int[] populationFitness = new int[populationSize]; // wartosci f. dopasowania
		int[][] selectedPopulation = new int[populationSize][graph.vertexAmount];
		int[][] generatedPopulation = generatePopulation(populationSize);
		long endTime, execTime;

		populationScore = populationScore(generatedPopulation);
		populationFitness = populationFitness(populationScore);

		do {
			populationFitness = populationFitness(populationScore);
			selectedPopulation = tournamentMethodSelection(generatedPopulation, populationFitness);
			selectedPopulation = crossoverOX(selectedPopulation);
			switch (mutationMethod) {
			case 1:
				selectedPopulation = transpositionMutation(selectedPopulation);
				break;
			case 2:
				selectedPopulation = insertionMutation(selectedPopulation);
				break;
			default:
				selectedPopulation = transpositionMutation(selectedPopulation);
				break;
			}
			
			generatedPopulation = createAndSortNewPopulation(selectedPopulation,generatedPopulation);
			populationScore = populationScore(generatedPopulation);

			endTime = System.nanoTime();
			execTime = (endTime - startTime) / 1000000000;
		} while (execTime <= maxTime);

		System.out.print("Najlepszy wynik:" + populationScore[0] + "\nSciezka: ");

		for (int i = 0; i < generatedPopulation[0].length; i++) {
			System.out.print(generatedPopulation[0][i] + "-");
		}
		System.out.println(generatedPopulation[0][0]);

		System.out.println("Czas dzialania: " + execTime + "s");

	}

	// Generuje losowo populacjê
	public static int[][] generatePopulation(int populationSize) {
		ArrayList<Integer> numberRange = new ArrayList<Integer>();
		for (int i = 0; i < graph.vertexAmount; i++) {
			numberRange.add(i);
		}

		int[][] generatedPopulation = new int[populationSize][graph.vertexAmount];

		for (int i = 0; i < generatedPopulation.length; i++) {
			Collections.shuffle(numberRange);
			for (int j = 0; j < generatedPopulation[i].length; j++) {
				generatedPopulation[i][j] = numberRange.get(j);
			}
		}

		return generatedPopulation;
	}

	// Oblicza wyniki(koszty tras) osobnikow w populacji
	public static int[] populationScore(int[][] population) {
		int sum = 0;
		int nodeA = 0;
		int nodeB = 0;
		int[] populationScore = new int[population.length];
		for (int i = 0; i < population.length; i++) {
			for (int j = 1; j < population[i].length; j++) {

				nodeA = population[i][j - 1];
				nodeB = population[i][j];
				sum += graph.getNeighborhoodMatrix()[nodeA][nodeB];
			}
			// Ostatnie przejscie ( do wierzcho³ka startowego)
			nodeA = population[i][population[i].length - 1];
			nodeB = population[i][0];
			sum += graph.getNeighborhoodMatrix()[nodeA][nodeB];
			populationScore[i] = sum;
			sum = 0;
		}
		return populationScore;
	}

	// Obliczanie wartosci funkcji dopasowania osobnikow
	public static int[] populationFitness(int[] populationScore) {

		int max = 0;
		int[] populationFitness = new int[populationScore.length];

		// szukanie najdrozszej sciezki
		for (int i = 0; i < populationScore.length; i++) {
			max = Math.max(max, populationScore[i]);
		}

		// funkcja dopasowania f = max - score[i] + 1
		for (int i = 0; i < populationScore.length; i++) {
			populationFitness[i] = max - populationScore[i] + 1;
		}

		return populationFitness;

	}

	// Turniejowa metoda selekcji
	public static int[][] tournamentMethodSelection(int[][] population, int[] populationFitness) {

		int[] randomNumbers = new int[tournamentSize];
		int[][] selectedPopulation = new int[populationSize/4][graph.vertexAmount];
		int indexOfBest = 0;

		for (int i = 0; i < populationSize/4; i++) {
			int max = 0;
			// losowanie grup dwu-osobnikowych oraz porownanie ich f.dopasowania
			for (int j = 0; j < tournamentSize; j++) {
				randomNumbers[j] = random.nextInt(populationSize);

				if (max < Math.max(max, populationFitness[randomNumbers[j]])) {
					max = Math.max(max, populationFitness[randomNumbers[j]]);
					indexOfBest = randomNumbers[j];
				}

			}
			selectedPopulation[i] = population[indexOfBest];
		}

		return selectedPopulation;
	}

	// Mutacja insertion - zmiana losowych wierzcholkow miejscami
	public static int[][] insertionMutation(int[][] population) {

		int[][] populationMutated = new int[population.length][graph.vertexAmount];
		int r1;
		int r2;
		populationMutated = population;

		for (int i = 0; i < populationMutated.length; i++) {
			double randomValue = random.nextDouble();
			if (randomValue < mutationProbability) {
				r1 = random.nextInt(populationMutated[i].length);
				do {
					r2 = random.nextInt(populationMutated[i].length);
				} while (r1 == r2 || r1 == r2 + 1 || r1 == r2 - 1);

				int temp = populationMutated[i][r2];
				if (r2 > r1) {
					for (int j = r2; j > r1; j--) {
						populationMutated[i][j] = populationMutated[i][j - 1];
					}
				} else {
					for (int j = r2; j < r1; j++) {
						populationMutated[i][j] = populationMutated[i][j + 1];
					}
				}
				populationMutated[i][r1] = temp;
			}
		}
		return populationMutated;
	}

	// Mutacja transposition - zmiana losowych wierzcholkow miejscami
	public static int[][] transpositionMutation(int[][] population) {

		int[][] populationMutated = new int[population.length][graph.vertexAmount];
		int r1;
		int r2;
		populationMutated = population;

		for (int i = 0; i < populationMutated.length; i++) {
			double randomValue = random.nextDouble();
			if (randomValue < mutationProbability) {
				r1 = random.nextInt(populationMutated[i].length);
				do {
					r2 = random.nextInt(populationMutated[i].length);
				} while (r1 == r2);

				int temp = populationMutated[i][r1];
				populationMutated[i][r1] = populationMutated[i][r2];
				populationMutated[i][r2] = temp;
			}
		}
		return populationMutated;
	}


	// Krzyzowanie PMX
	public static int[][] crossoverOX(int[][] population) {

		int k1 = 0;
		int k2 = 0;
		int randomIndex1 = 0;
		int randomIndex2 = 0;

		ArrayList<int[]> newPopulation = new ArrayList<int[]>();

		for (int a = 0; a < population.length; a++) {
			double randomValue = random.nextDouble();
			if (randomValue < crossoverProbability) {
				// losowanie punktow krzyzowania
				k1 = random.nextInt(population[0].length / 2);
				k2 = random.nextInt(population[0].length / 2) + population[0].length / 2;

				// losowanie osobnikow do krzyzowania
				randomIndex1 = random.nextInt(population.length);
				int[] parent1 = population[randomIndex1];
				do {
					randomIndex2 = random.nextInt(population.length);
				} while (randomIndex1 == randomIndex2);

				int[] parent2 = population[randomIndex2];

				int[] child1 = new int[population[0].length];
				int[] child2 = new int[population[0].length];

				for (int i = 0; i < child1.length; i++) {
					child1[i] = -1;
					child2[i] = -1;
				}
				for (int i = k1; i < k2; i++) {
					child1[i] = parent2[i];
					child2[i] = parent1[i];
				}
				// tworzenie pierwszego potomka
				for (int i = 0; i < child1.length - (k2 - k1); i++) {
					for (int j = 0; j < child1.length; j++) {
						boolean isAvailable = true;
						for (int l = 0; l < child1.length; l++) {
							if (parent1[(k2 + j) % child1.length] == child1[l]) {
								isAvailable = false;
								break;
							}
						}
						if (isAvailable) {
							child1[(k2 + i) % child1.length] = parent1[(k2 + j) % child1.length];
							break;
						}

					}
				}
				for (int i = 0; i < child2.length - (k2 - k1); i++) {
					for (int j = 0; j < child2.length; j++) {
						boolean isAvailable = true;
						for (int l = 0; l < child2.length; l++) {
							if (parent2[(k2 + j) % child2.length] == child2[l]) {
								isAvailable = false;
								break;
							}
						}
						if (isAvailable) {
							child2[(k2 + i) % child2.length] = parent2[(k2 + j) % child2.length];
							break;
						}
					}
				}
				newPopulation.add(child1);
				newPopulation.add(child2);
			}
		}
		int[][] crossedPopulation = new int[newPopulation.size()][population[0].length];

		for (int i = 0; i < crossedPopulation.length; i++) {
			crossedPopulation[i] = newPopulation.get(i);
		}

		return crossedPopulation;
	}

	// Utworzenie nowej populacji poprzed obciêcie
	public static int[][] createAndSortNewPopulation(int[][] selectedPopulation, int[][] generatedPopulation) {

		int[] selectedPopulationScore = populationScore(selectedPopulation);
		int[] generatedPopulationScore = populationScore(generatedPopulation);
		int[][] populationSorted = new int[populationSize][selectedPopulation[0].length];
		ArrayList<Integer> scoresArrayList = new ArrayList<Integer>();
		
		for (int i = 0; i < selectedPopulation.length; i++) {
			scoresArrayList.add(selectedPopulationScore[i]);
		}
		for (int i = 0; i < generatedPopulation.length; i++) {
			scoresArrayList.add(generatedPopulationScore[i]);
		}
		
		HashMap<Integer, int[]> scoreAndPopulation = new HashMap<Integer, int[]>();

		for (int i = 0; i < generatedPopulation.length; i++) {
			scoreAndPopulation.put(generatedPopulationScore[i], generatedPopulation[i]);
		}

		for (int i = 0; i < selectedPopulation.length; i++) {
			scoreAndPopulation.put(selectedPopulationScore[i], selectedPopulation[i]);

		}
		Collections.sort(scoresArrayList);
		for (int i = 0; i < populationSorted.length; i++) {
			populationSorted[i] = scoreAndPopulation.get(scoresArrayList.get(i));
		}
		return populationSorted;
	}

	
}
