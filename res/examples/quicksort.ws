This_part_reads_in_an_array_of_integers._Read_in_number_of_integers_and_store_it_at_0:_PUSH  0  
CALL_SUBROUTINE
 	10 	 	 
READ_INT	
		Address_of_the_array_stored_at_1:_PUSH  1 	
PUSH  100 		  	  
STORE		 Numbers_already_read:_PUSH  0  
This_label_reads_an_integer:_MARK_LABEL
  0  
Duplicate_current_number:_DUPLICATE 
 Check_if_all_integers_are_read:_PUSH  0  
RETRIEVE			SUBTRACT	  	GOTO_IF_ZERO
	 1 	
Get_current_array_index:_DUPLICATE 
 PUSH  1 	
RETRIEVE			ADD	   Read_and_store_value:_CALL_SUBROUTINE
 	10 	 	 
READ_INT	
		Increment:_PUSH  1 	
ADD	   GOTO
 
0  
MARK_LABEL
  1 	
Pop_the_current_number:_POP 

Print_out_array:_CALL_SUBROUTINE
 	11 	 		
Quicksort_:Load_left:_PUSH  1 	
RETRIEVE			Load_right:_DUPLICATE 
 PUSH  0  
RETRIEVE			ADD	   PUSH  1 	
SUBTRACT	  	Run_quicksort:_CALL_SUBROUTINE
 	2 	 
Print_out_array:_CALL_SUBROUTINE
 	11 	 		
EXIT


ERROR_Stack_elements_must_be_popped_somewhere!_Quicksort_subroutine_(takes_left_and_right_index_as_input,_left_pushed_first):_MARK_LABEL
  2 	 
Duplicate_left_and_right:_COPY_N 	 1 	
COPY_N 	 1 	
Test_if_left<right:_CALL_SUBROUTINE
 	30 				 
GOTO_IF_ZERO
	 3 		
RETURN
	
Do_quicksort:_MARK_LABEL
  3 		
Duplicate_left_and_right:_COPY_N 	 1 	
COPY_N 	 1 	
Divide_and_return_divider:_CALL_SUBROUTINE
 	40 	 	   
#DBG_BREAKQuicksort_left_divider-1:_COPY_N 	 2 	 
COPY_N 	 1 	
PUSH  1 	
SUBTRACT	  	CALL_SUBROUTINE
 	2 	 
#DBG_BREAKQuicksort_divider+1_right:_DUPLICATE 
 PUSH  1 	
ADD	   COPY_N 	 2 	 
CALL_SUBROUTINE
 	2 	 
POP 

POP 

POP 

POP 

POP 

RETURN
	
Prints_out_a_number_request:_MARK_LABEL
  10 	 	 
GENERATED_OUTPUT_START   	  			 
	
     			 	 	
	
     		 		 	
	
     		   	 
	
     		  	 	
	
     			  	 
	
     			 	 
	
     	     
	
  END_RETURN
	
Prints_out_the_values_of_the_array:_MARK_LABEL
  11 	 		
Bracket:_PUSH  [ 	 		 		
PRINT_CHAR	
  Current_index:_PUSH  0  
MARK_LABEL
  12 		  
DUPLICATE 
 Test_if_end_is_reached:_PUSH  0  
RETRIEVE			SUBTRACT	  	If_so,_return:_GOTO_IF_ZERO
	 20 	 	  
Get_the_start_pos_of_the_array:_DUPLICATE 
 PUSH  1 	
RETRIEVE			Calculate_the_value_pos:_ADD	   Get_the_value_and_print_it:_RETRIEVE			PRINT_INT	
 	Print_comma:_PUSH  , 	 		  
PRINT_CHAR	
  Print_space:_PUSH    	     
PRINT_CHAR	
  Increment_and_go_back:_PUSH  1 	
ADD	   GOTO
 
12 		  
This_pops_the_stack_and_returns:_MARK_LABEL
  20 	 	  
Print_bracket:_PUSH  ] 	 			 	
PRINT_CHAR	
  Print_linefeed:_PUSH  10 	 	 
PRINT_CHAR	
  POP 

RETURN
	
Returns_0_if_the_first_pushed_number_is_smaller_than_the_second:_MARK_LABEL
  30 				 
SUBTRACT	  	GOTO_IF_NEGATIVE
		31 					
PUSH  1 	
RETURN
	
MARK_LABEL
  31 					
PUSH  0  
RETURN
	
Returns_0_if_the_first_pushed_number_is_greater_than_the_second:_MARK_LABEL
  32 	     
SWAP 
	GOTO
 
30 				 
Returns_0_if_the_first_pushed_number_is_smaller_or_equal_than_the_second:_MARK_LABEL
  34 	   	 
SUBTRACT	  	DUPLICATE 
 GOTO_IF_NEGATIVE
		35 	   		
GOTO_IF_ZERO
	 36 	  	  
PUSH  1 	
RETURN
	
MARK_LABEL
  35 	   		
POP 

MARK_LABEL
  36 	  	  
PUSH  0  
RETURN
	
Returns_0_if_the_first_pushed_number_is_greater_or_equal_than_the_second:_MARK_LABEL
  38 	  		 
SWAP 
	GOTO
 
34 	   	 
Divide_subroutine(left,right):_MARK_LABEL
  40 	 	   
Store_right_in_3:_PUSH  3 		
SWAP 
	STORE		 Store_left_in_2:_PUSH  2 	 
SWAP 
	STORE		 Store_pivot_in_4:_PUSH  4 	  
PUSH  3 		
RETRIEVE			RETRIEVE			STORE		 Push_a_and_b:_PUSH  2 	 
RETRIEVE			PUSH  3 		
RETRIEVE			PUSH  1 	
SUBTRACT	  	Loop_1:_MARK_LABEL
  41 	 	  	
Copy_a_to_top:_COPY_N 	 1 	
Load_data[a]:_RETRIEVE			Load_pivot:_PUSH  4 	  
RETRIEVE			Test_if_data[a]<=pivot:_CALL_SUBROUTINE
 	32 	     
GOTO_IF_ZERO
	 42 	 	 	 
Push_a_to_top:_COPY_N 	 1 	
Load_right:_PUSH  3 		
RETRIEVE			CALL_SUBROUTINE
 	38 	  		 
GOTO_IF_ZERO
	 42 	 	 	 
Increment_a:_SWAP 
	PUSH  1 	
ADD	   SWAP 
	GOTO
 
41 	 	  	
Loop_2:_MARK_LABEL
  42 	 	 	 
Copy_b_and_load_data[b]:_DUPLICATE 
 RETRIEVE			Load_pivot:_PUSH  4 	  
RETRIEVE			CALL_SUBROUTINE
 	30 				 
GOTO_IF_ZERO
	 43 	 	 		
Duplicate_b:_DUPLICATE 
 Load_left:_PUSH  2 	 
RETRIEVE			CALL_SUBROUTINE
 	34 	   	 
GOTO_IF_ZERO
	 43 	 	 		
Decrement_b:_PUSH  1 	
SUBTRACT	  	GOTO
 
42 	 	 	 
MARK_LABEL
  43 	 	 		
Duplicate_a_and_b:_COPY_N 	 1 	
COPY_N 	 1 	
Test_if_a>=b:_CALL_SUBROUTINE
 	38 	  		 
GOTO_IF_ZERO
	 50 		  	 
Swap_data[a/b]:_Copy_a_to_top_and_load:_COPY_N 	 1 	
RETRIEVE			Copy_b_to_top_and_load:_COPY_N 	 1 	
RETRIEVE			Copy_a_to_top,_swap_and_store:_COPY_N 	 3 		
SWAP 
	STORE		 Copy_b_to_top,_swap_and_store:_COPY_N 	 1 	
SWAP 
	STORE		 Test_if_a<b,_loop_if_so:_MARK_LABEL
  50 		  	 
Duplicate_a_and_b:_COPY_N 	 1 	
COPY_N 	 1 	
CALL_SUBROUTINE
 	30 				 
GOTO_IF_ZERO
	 41 	 	  	
Load_data[a]:_COPY_N 	 1 	
RETRIEVE			Load_pivot:_PUSH  4 	  
RETRIEVE			CALL_SUBROUTINE
 	32 	     
GOTO_IF_ZERO
	 51 		  		
Returns_a:_MARK_LABEL
  52 		 	  
POP 

RETURN
	
MARK_LABEL
  51 		  		
Copy_a_and_right_to_top:_COPY_N 	 1 	
PUSH  3 		
RETRIEVE			Load_data[a]:_COPY_N 	 1 	
RETRIEVE			Store_in_right:_COPY_N 	 1 	
SWAP 
	STORE		 Load_data[right]_and_store_in_a:_RETRIEVE			STORE		 GOTO
 
52 		 	  
