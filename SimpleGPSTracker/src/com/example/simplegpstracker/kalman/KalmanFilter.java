package com.example.simplegpstracker.kalman;

public class KalmanFilter {
	/* k */
	int timestep;

	/* These parameters define the size of the matrices. */
	int state_dimension, observation_dimension;

	/* This group of matrices must be specified by the user. */
	/* F_k */
	MatrixOne state_transition;
	/* H_k */
	MatrixOne observation_model;
	/* Q_k */
	MatrixOne process_noise_covariance;
	/* R_k */
	MatrixOne observation_noise_covariance;

	/* The observation is modified by the user before every time step. */
	/* z_k */
	MatrixOne observation;

	/* This group of matrices are updated every time step by the filter. */
	/* x-hat_k|k-1 */
	MatrixOne predicted_state;
	/* P_k|k-1 */
	MatrixOne predicted_estimate_covariance;
	/* y-tilde_k */
	MatrixOne innovation;
	/* S_k */
	MatrixOne innovation_covariance;
	/* S_k^-1 */
	MatrixOne inverse_innovation_covariance;
	/* K_k */
	MatrixOne optimal_gain;
	/* x-hat_k|k */
	MatrixOne state_estimate;
	/* P_k|k */
	MatrixOne estimate_covariance;

	/* This group is used for meaningless intermediate calculations */
	MatrixOne vertical_scratch;
	MatrixOne small_square_scratch;
	MatrixOne big_square_scratch;

	public KalmanFilter(int state_dimension, int observation_dimension) {
		timestep = 0;
		this.state_dimension = state_dimension;
		this.observation_dimension = observation_dimension;

		state_transition = new MatrixOne(state_dimension, state_dimension);
		observation_model = new MatrixOne(observation_dimension, state_dimension);
		process_noise_covariance = new MatrixOne(state_dimension, state_dimension);
		observation_noise_covariance = new MatrixOne(observation_dimension, observation_dimension);

		observation = new MatrixOne(observation_dimension, 1);

		predicted_state = new MatrixOne(state_dimension, 1);
		predicted_estimate_covariance = new MatrixOne(state_dimension,	state_dimension);
		innovation = new MatrixOne(observation_dimension, 1);
		innovation_covariance = new MatrixOne(observation_dimension, observation_dimension);
		inverse_innovation_covariance = new MatrixOne(observation_dimension, observation_dimension);
		optimal_gain = new MatrixOne(state_dimension, observation_dimension);
		state_estimate = new MatrixOne(state_dimension, 1);
		estimate_covariance = new MatrixOne(state_dimension, state_dimension);

		vertical_scratch = new MatrixOne(state_dimension, observation_dimension);
		small_square_scratch = new MatrixOne(observation_dimension,observation_dimension);
		big_square_scratch = new MatrixOne(state_dimension, state_dimension);
	}

	/*
	 * Runs one timestep of prediction + estimation.
	 * 
	 * Before each time step of running this, set f.observation to be the next
	 * time step's observation.
	 * 
	 * Before the first step, define the model by setting: f.state_transition
	 * f.observation_model f.process_noise_covariance
	 * f.observation_noise_covariance
	 * 
	 * It is also advisable to initialize with reasonable guesses for
	 * f.state_estimate f.estimate_covariance
	 */
	void update() {
		predict();
		estimate();
	}

	/* Just the prediction phase of update. */
	void predict() {
		timestep++;

		/* Predict the state */
		MatrixOne.multiply_matrix(state_transition, state_estimate, predicted_state);

		/* Predict the state estimate covariance */
		MatrixOne.multiply_matrix(state_transition, estimate_covariance, big_square_scratch);
		MatrixOne.multiply_by_transpose_matrix(big_square_scratch, state_transition, predicted_estimate_covariance);
		MatrixOne.add_matrix(predicted_estimate_covariance, process_noise_covariance, predicted_estimate_covariance);
	}

	/* Just the estimation phase of update. */
	void estimate() {
		/* Calculate innovation */
		MatrixOne.multiply_matrix(observation_model, predicted_state, innovation);
		MatrixOne.subtract_matrix(observation, innovation, innovation);

		/* Calculate innovation covariance */
		MatrixOne.multiply_by_transpose_matrix(predicted_estimate_covariance, observation_model, vertical_scratch);
		MatrixOne.multiply_matrix(observation_model, vertical_scratch, innovation_covariance);
		MatrixOne.add_matrix(innovation_covariance, observation_noise_covariance, innovation_covariance);

		/*
		 * Invert the innovation covariance. Note: this destroys the innovation
		 * covariance. TODO: handle inversion failure intelligently.
		 */
		MatrixOne.destructive_invert_matrix(innovation_covariance, inverse_innovation_covariance);

		/*
		 * Calculate the optimal Kalman gain. Note we still have a useful
		 * partial product in vertical scratch from the innovation covariance.
		 */
		MatrixOne.multiply_matrix(vertical_scratch, inverse_innovation_covariance, optimal_gain);

		/* Estimate the state */
		MatrixOne.multiply_matrix(optimal_gain, innovation, state_estimate);
		MatrixOne.add_matrix(state_estimate, predicted_state, state_estimate);

		/* Estimate the state covariance */
		MatrixOne.multiply_matrix(optimal_gain, observation_model, big_square_scratch);
		MatrixOne.subtract_from_identity_matrix(big_square_scratch);
		MatrixOne.multiply_matrix(big_square_scratch, predicted_estimate_covariance, estimate_covariance);
	}
}
