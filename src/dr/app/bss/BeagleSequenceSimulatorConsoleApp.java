package dr.app.bss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import dr.app.beagle.tools.BeagleSequenceSimulator;
import dr.app.beagle.tools.Partition;
import dr.app.util.Arguments;
import dr.math.MathUtils;

public class BeagleSequenceSimulatorConsoleApp {

	private Arguments arguments;
	private PartitionData data;
	private PartitionDataList dataList;
	
	private static final String SPLIT_PARTITION = ":";
//	private static final String SEED = "-seed";	
	
	private static final String HELP = "help";
	private static final String TREE_MODEL = "treeModel";

	private static final String BRANCH_SUBSTITUTION_MODEL = "branchSubstitutionModel";
	private static final String HKY = PartitionData.substitutionModels[0];
	private static final String HKY_SUBSTITUTION_PARAMETER_VALUES = "HKYsubstitutionParameterValues";
	private static final String GTR = PartitionData.substitutionModels[1];
	private static final String GTR_SUBSTITUTION_PARAMETER_VALUES = "GTRsubstitutionParameterValues";
	private static final String TN93 = PartitionData.substitutionModels[2];
	private static final String TN93_SUBSTITUTION_PARAMETER_VALUES = "TN93substitutionParameterValues";
	private static final String GY94_CODON_MODEL = PartitionData.substitutionModels[3];
	private static final String GY94_SUBSTITUTION_PARAMETER_VALUES = "GY94substitutionParameterValues";
	
	private static final String SITE_RATE_MODEL = "siteRateModel";
	private static final String NO_MODEL = "NoModel";
	private static final String GAMMA_SITE_RATE_MODEL = "gammaSiteRateModel";
	private static final String GAMMA_SITE_RATE_MODEL_PARAMETER_VALUES = "gammaSiteRateModelParameterValues";
	
	private static final String CLOCK_RATE_MODEL = "clockRateModel";
	private static final String STRICT_CLOCK = "strictClock";
	private static final String STRICT_CLOCK_PARAMETER_VALUES = "strictClockParameterValues";
	private static final String LOGNORMAL_RELAXED_CLOCK = "lognormalRelaxedClock";
	private static final String LOGNORMAL_RELAXED_CLOCK_PARAMETER_VALUES = "lognormalRelaxedClockParameterValues";
	private static final String EXPONENTIAL_RELAXED_CLOCK = "exponentialRelaxedClock";
	private static final String EXPONENTIAL_RELAXED_CLOCK_PARAMETER_VALUES = "exponentialRelaxedClockParameterValues";
	private static final String INVERSE_GAUSSIAN_RELAXED_CLOCK = "inverseGaussianRelaxedClock";
	private static final String INVERSE_GAUSSIAN_RELAXED_CLOCK_PARAMETER_VALUES = "inverseGaussianRelaxedClockParameterValues";
	
	private static final String FREQUENCY_MODEL = "frequencyModel";
	private static final String NUCLEOTIDE_FREQUENCIES = "nucleotideFrequencies";
	private static final String NUCLEOTIDE_FREQUENCY_PARAMETER_VALUES = "nucleotideFrequencyParameterValues";
	private static final String CODON_FREQUENCIES = "codonFrequencies";
	private static final String CODON_FREQUENCY_PARAMETER_VALUES = "codonFrequencyParameterValues";
	
	private static final String FROM = "from";
	private static final String TO = "to";
	private static final String EVERY = "every";

	public BeagleSequenceSimulatorConsoleApp() {

		data = new PartitionData();
		dataList =  new PartitionDataList();
		
		// //////////////////
		// ---DEFINITION---//
		// //////////////////

		arguments = new Arguments(
				new Arguments.Option[] {

						new Arguments.Option(HELP,
								"print this information and exit"),

						new Arguments.StringOption(TREE_MODEL, "tree model",
								"specify tree topology"),

						new Arguments.StringOption(BRANCH_SUBSTITUTION_MODEL,
								new String[] { HKY, //
										GTR, //
										TN93, //
										GY94_CODON_MODEL //
								}, false, "specify substitution model"),

								new Arguments.RealArrayOption(HKY_SUBSTITUTION_PARAMETER_VALUES, 1, "specify HKY substitution model parameter values"),
								new Arguments.RealArrayOption(GTR_SUBSTITUTION_PARAMETER_VALUES, 6, "specify GTR substitution model parameter values"),
								new Arguments.RealArrayOption(TN93_SUBSTITUTION_PARAMETER_VALUES, 2, "specify TN93 substitution model parameter values"),
								new Arguments.RealArrayOption(GY94_SUBSTITUTION_PARAMETER_VALUES, 2, "specify GY94 substitution model parameter values"),
								
						new Arguments.StringOption(SITE_RATE_MODEL,
								new String[] { NO_MODEL, //
										GAMMA_SITE_RATE_MODEL, //
								}, false, "specify site rate model"),

								new Arguments.RealArrayOption(GAMMA_SITE_RATE_MODEL_PARAMETER_VALUES, 2, "specify Gamma Site Rate Model parameter values"),
								
						new Arguments.StringOption(CLOCK_RATE_MODEL,
								new String[] { STRICT_CLOCK, //
										LOGNORMAL_RELAXED_CLOCK, //
										EXPONENTIAL_RELAXED_CLOCK, //
										INVERSE_GAUSSIAN_RELAXED_CLOCK
								}, false, "specify clock rate model"),

								new Arguments.RealArrayOption(STRICT_CLOCK_PARAMETER_VALUES, 1, "specify Strict Clock parameter values"),
								new Arguments.RealArrayOption(LOGNORMAL_RELAXED_CLOCK_PARAMETER_VALUES, 3, "specify Lognormal Relaxed Clock parameter values"),
								new Arguments.RealArrayOption(EXPONENTIAL_RELAXED_CLOCK_PARAMETER_VALUES, 2, "specify Exponential Relaxed Clock parameter values"),
								new Arguments.RealArrayOption(INVERSE_GAUSSIAN_RELAXED_CLOCK_PARAMETER_VALUES, 3, "specify Inverse Gaussia Relaxed Clock parameter values"),
								
						new Arguments.StringOption(FREQUENCY_MODEL,
								new String[] { NUCLEOTIDE_FREQUENCIES, //
										CODON_FREQUENCIES, //
								}, false, "specify frequency model"),

								new Arguments.RealArrayOption(NUCLEOTIDE_FREQUENCY_PARAMETER_VALUES, 4, "specify Strict Clock parameter values"),
								new Arguments.RealArrayOption(CODON_FREQUENCY_PARAMETER_VALUES, 61, "specify Strict Clock parameter values"),
								
				new Arguments.IntegerOption(FROM, "specify 'from' attribute"),
				new Arguments.IntegerOption(TO, "specify 'to' attribute"),
				new Arguments.IntegerOption(EVERY, "specify 'every' attribute")

		});

	}// END: constructor
	
	public void simulate(String[] args) {

		try {

			// /////////////////////////////////
			// ---SPLIT PARTITION ARGUMENTS---//
			// /////////////////////////////////

			int from = 0;
			int to = 0;
			ArrayList<String[]> argsList = new ArrayList<String[]>();
			for (String arg : args) {

				if (arg.equalsIgnoreCase(SPLIT_PARTITION)) {
					argsList.add(Arrays.copyOfRange(args, from, to));
					from = to + 1;
				}// END: split check

				to++;
			}// END: args loop

			if (args[0].contains(HELP)) {

				gracefullyExit(null);

			} else if (argsList.size() == 0) {

				gracefullyExit("Empty or incorrect arguments list.");

			}// END: failed split check
			
			String[] leftoverArguments = Arrays.copyOfRange(args, from, args.length);
			if (leftoverArguments.length > 2) {
				gracefullyExit("Unrecognized option " + leftoverArguments[2]);
			}
	
			ArrayList<Partition> partitionsList = new ArrayList<Partition>();
			for (String partitionArgs[] : argsList) {

				// /////////////
				// ---PARSE---//
				// /////////////

				arguments.parseArguments(partitionArgs);

				// ///////////////////
				// ---INTERROGATE---//
				// ///////////////////

				String option = null;
				double[] values = null;

				if (partitionArgs.length == 0 || arguments.hasOption(HELP)) {
					
					gracefullyExit(null);
					
				}// END: HELP option check

				// Tree Model
				if (arguments.hasOption(TREE_MODEL)) {

					data.treeFile = new File(
							arguments.getStringOption(TREE_MODEL));

				} else {

					throw new RuntimeException("TreeModel not specified.");

				}// END: TREE_MODEL option check

				// Branch Substitution Model
				if (arguments.hasOption(BRANCH_SUBSTITUTION_MODEL)) {

					option = arguments
							.getStringOption(BRANCH_SUBSTITUTION_MODEL);

					if (option.equalsIgnoreCase(HKY)) {

						int index = 0;
						data.substitutionModelIndex = index;

						if (arguments
								.hasOption(HKY_SUBSTITUTION_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(HKY_SUBSTITUTION_PARAMETER_VALUES);
							parseSubstitutionValues(index, values);
						}

					} else if (option.equalsIgnoreCase(GTR)) {

						int index = 1;
						data.substitutionModelIndex = index;

						if (arguments
								.hasOption(GTR_SUBSTITUTION_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(GTR_SUBSTITUTION_PARAMETER_VALUES);
							parseSubstitutionValues(index, values);
						}

					} else if (option.equalsIgnoreCase(TN93)) {

						int index = 2;
						data.substitutionModelIndex = index;

						if (arguments
								.hasOption(TN93_SUBSTITUTION_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(TN93_SUBSTITUTION_PARAMETER_VALUES);
							parseSubstitutionValues(index, values);
						}

					} else if (option.equalsIgnoreCase(GY94_CODON_MODEL)) {

						int index = 3;
						data.substitutionModelIndex = index;

						if (arguments
								.hasOption(GY94_SUBSTITUTION_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(GY94_SUBSTITUTION_PARAMETER_VALUES);
							parseSubstitutionValues(index, values);
						}

					} else {
						gracefullyExit("Unrecognized option.");
					}

				}// END: BRANCH_SUBSTITUTION_MODEL option check

				// Site Rate Model
				if (arguments.hasOption(SITE_RATE_MODEL)) {

					option = arguments.getStringOption(SITE_RATE_MODEL);

					if (option.equalsIgnoreCase(NO_MODEL)) {

						int index = 0;
						data.siteRateModelIndex = index;

					} else if (option.equalsIgnoreCase(GAMMA_SITE_RATE_MODEL)) {

						int index = 1;
						data.siteRateModelIndex = index;

						if (arguments
								.hasOption(GAMMA_SITE_RATE_MODEL_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(GAMMA_SITE_RATE_MODEL_PARAMETER_VALUES);
							parseSiteRateValues(index, values);
						}

					} else {
						gracefullyExit("Unrecognized option.");
					}

				}// END: SITE_RATE_MODEL option check

				// Clock Rate Model
				if (arguments.hasOption(CLOCK_RATE_MODEL)) {

					option = arguments.getStringOption(CLOCK_RATE_MODEL);

					if (option.equalsIgnoreCase(STRICT_CLOCK)) {

						int index = 0;
						data.clockModelIndex = index;
						if (arguments.hasOption(STRICT_CLOCK_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(STRICT_CLOCK_PARAMETER_VALUES);
							parseClockValues(index, values);
						}

					} else if (option.equalsIgnoreCase(LOGNORMAL_RELAXED_CLOCK)) {

						int index = 1;
						data.clockModelIndex = index;
						if (arguments
								.hasOption(LOGNORMAL_RELAXED_CLOCK_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(LOGNORMAL_RELAXED_CLOCK_PARAMETER_VALUES);
							parseClockValues(index, values);
						}

					} else if (option
							.equalsIgnoreCase(EXPONENTIAL_RELAXED_CLOCK)) {

						int index = 2;
						data.clockModelIndex = index;
						if (arguments
								.hasOption(EXPONENTIAL_RELAXED_CLOCK_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(EXPONENTIAL_RELAXED_CLOCK_PARAMETER_VALUES);
							parseClockValues(index, values);
						}
						
					} else if(option
							.equalsIgnoreCase(INVERSE_GAUSSIAN_RELAXED_CLOCK)) { 
					
						int index = 3;
						data.clockModelIndex = index;
						if (arguments
								.hasOption(INVERSE_GAUSSIAN_RELAXED_CLOCK_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(INVERSE_GAUSSIAN_RELAXED_CLOCK_PARAMETER_VALUES);
							parseClockValues(index, values);
						}
					
					} else {
						gracefullyExit("Unrecognized option.");
					}

				}// END: CLOCK_RATE_MODEL option check

				// Frequency Model
				if (arguments.hasOption(FREQUENCY_MODEL)) {

					option = arguments.getStringOption(FREQUENCY_MODEL);

					if (option.equalsIgnoreCase(NUCLEOTIDE_FREQUENCIES)) {

						int index = 0;
						data.frequencyModelIndex = index;
						if (arguments
								.hasOption(NUCLEOTIDE_FREQUENCY_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(NUCLEOTIDE_FREQUENCY_PARAMETER_VALUES);
							parseFrequencyValues(index, values);
						}

					} else if (option.equalsIgnoreCase(CODON_FREQUENCIES)) {

						int index = 1;
						data.frequencyModelIndex = index;
						if (arguments
								.hasOption(CODON_FREQUENCY_PARAMETER_VALUES)) {
							values = arguments
									.getRealArrayOption(CODON_FREQUENCY_PARAMETER_VALUES);
							parseFrequencyValues(index, values);
						}

					} else {
						gracefullyExit("Unrecognized option.");
					}

				}// END: FREQUENCY_MODEL option check

				if (arguments.hasOption(FROM)) {

					data.from = arguments.getIntegerOption(FROM);

				}// END: FROM option check

				if (arguments.hasOption(TO)) {

					data.to = arguments.getIntegerOption(TO);

				}// END: TO option check

				if (arguments.hasOption(EVERY)) {

					data.every = arguments.getIntegerOption(EVERY);

				}// END: EVERY option check

				dataList.add(data);
				
				// create partition
				Partition partition = new Partition(data.createTreeModel(), //
						data.createBranchModel(), //
						data.createSiteRateModel(), //
						data.createClockRateModel(), //
						data.createFrequencyModel(), //
						data.from - 1, // from
						data.to - 1, // to
						data.every // every
				);

				partitionsList.add(partition);

			}// END: partitionArgs loop

			// ////////////////
			// ---SIMULATE---//
			// ////////////////
			
			if (Utils.VERBOSE) {
				Utils.printPartitionDataList(dataList);
				System.out.println();
			}
			
			String outputFile = null;
			if (leftoverArguments.length > 0) {
				outputFile = leftoverArguments[0];
			} else {
				outputFile = "output.fasta";
			}
			
			if (leftoverArguments.length > 1) {
				dataList.startingSeed = Long.parseLong(leftoverArguments[1]);
				dataList.setSeed = true;
			}
			
			if (dataList.setSeed) {
				MathUtils.setSeed(dataList.startingSeed);
			}
			
			BeagleSequenceSimulator beagleSequenceSimulator = new BeagleSequenceSimulator(
					partitionsList);
			
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
			writer.println(beagleSequenceSimulator.simulate().toString());
			writer.close();
			
		} catch (Arguments.ArgumentException ae) {

			System.out.println();
			System.out.println(ae.getMessage());
			System.out.println();
			printUsage(arguments);
			System.exit(1);

		} catch (IOException ioe) {

			System.out.println();
			System.out.println(ioe.getMessage());
			System.out.println();
			printUsage(arguments);
			System.exit(1);

		}// END: try-catch block

	}// END: simulate

	private void parseSubstitutionValues(int substitutionModelIndex,
			double[] values) {
		for (int i = 0; i < PartitionData.substitutionParameterIndices[substitutionModelIndex].length; i++) {

			int k = PartitionData.substitutionParameterIndices[substitutionModelIndex][i];
			data.substitutionParameterValues[k] = values[i];

		}
	}// END: parseSubstitutionValues
	
	private void parseSiteRateValues(int siteRateModelIndex,
			double[] values) {
		for (int i = 0; i < PartitionData.siteRateModelParameterIndices[siteRateModelIndex].length; i++) {

			int k = PartitionData.siteRateModelParameterIndices[siteRateModelIndex][i];
			data.siteRateModelParameterValues[k] = values[i];

		}
	}// END: parseSiteRateModelParameterValues
	
	private void parseClockValues(int clockModelIndex,
			double[] values) {
		for (int i = 0; i < PartitionData.clockParameterIndices[clockModelIndex].length; i++) {

			int k = PartitionData.clockParameterIndices[clockModelIndex][i];
			data.clockParameterValues[k] = values[i];

		}
	}// END: parseClockValues
	
	private void parseFrequencyValues(int frequencyModelIndex,
			double[] values) {
		for (int i = 0; i < data.frequencyParameterIndices[frequencyModelIndex].length; i++) {

			int k = data.frequencyParameterIndices[frequencyModelIndex][i];
			data.frequencyParameterValues[k] = values[i];

		}
	}// END: parseFrequencyValues

	private void gracefullyExit(String message) {
		if (message != null) {
			System.out.println(message);
			System.out.println();
		}
		printUsage(arguments);
		System.exit(0);
	}// END: gracefullyExit

	private void printUsage(Arguments arguments) {

		arguments.printUsage(
				"java -Djava.library.path=/usr/local/lib -jar bss.jar", " "
						+ SPLIT_PARTITION + " " + "[<output-file-name>] [<seed>]");
		System.out.println();
		System.out
				.println("  Example: java -Djava.library.path=/usr/local/lib -jar bss.jar "
						+ "-treeModel /home/filip/SimTree.figtree "
						+ "-branchSubstitutionModel GTR -GTRsubstitutionParameterValues 10 10 10 10 10 10 "
						+ "-siteRateModel GammaSiteRateModel -gammaSiteRateModelParameterValues 1 1 "
						+ "-clockRateModel StrictClock -strictClockParameterValues 0.15 "
						+ "-frequencyModel NucleotideFrequencies -nucleotideFrequencyParameterValues 0.24 0.26 0.25 0.25 "
						+ "-from 1 "
						+ "-to 10 "
						+ "-every 1"
						+ " "
						+ SPLIT_PARTITION + " " + "sequences.fasta 123");
		System.out
				.println("  Multiple partitions example: java -Djava.library.path=/usr/local/lib -jar bss.jar"
						+ "-treeModel /home/filip/SimTree.figtree -from 1 -to 5 -every 1 -branchSubstitutionModel HKY -HKYsubstitutionParameterValues 1.0"
						+ " "
						+ SPLIT_PARTITION
						+ " "
						+ "-treeModel /home/filip/SimTree.figtree -from 6 -to 8 -every 1 -branchSubstitutionModel HKY -HKYsubstitutionParameterValues 10.0"
						+ " "
						+ SPLIT_PARTITION
						+ " "
						+ "-treeModel /home/filip/SimTree.figtree -from 9 -to 10 -every 1 -branchSubstitutionModel HKY -HKYsubstitutionParameterValues 1.0"
						+ " " + SPLIT_PARTITION + " " + "sequences.fasta");
		System.out.println();
	}// END: printUsage

}// END: class