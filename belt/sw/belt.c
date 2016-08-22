/*
	Hurry project belt main program.
	compile command: gcc belt.c -lbluetooth
	conduct command: ./a.out
*/
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include "/usr/include/bluetooth/bluetooth.h"
#include "/usr/include/bluetooth/rfcomm.h"
#include "/usr/include/bluetooth/sdp.h"
#include "/usr/include/bluetooth/sdp_lib.h"
#include <pthread.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>
#include "SCS.c"
#define PIN_HRYLIGHT 60
#define PIN_CNTLIGHT 48
#define PIN_HRYBTN 30
#define PIN_ENDBTN 31
/*----------------Pin direction----------------------------------------
		hurry 	LED 	<=>		P9_12	<=>		GPIO 60
		connect LED 	<=>		P9_15	<=>		GPIO 48

		hurry 	BTN 	<=>		P9_11	<=>		GPIO 30
		end 	BTN 	<=>		P9_13	<=>		GPIO 31
*/
//----------------variable---------------------------------------------
int mChannel = 0; 						// the read channel to regist the SDP
int localSocket, clientSocket; 			// socket
char msg_send[10] = "0";				// message would be sent to the phone

//----------------physical IO------------------------------------------
FILE *in1, *in2;						// the export file descriptors						
FILE *indir1, *indir2; 					// the direction file descriptors

//---------function declaration----------------------------------------
sdp_session_t* register_service(int );	// definite the SDP regist process
void endWholeProgram(sdp_session_t* );	// define the terminal of the program
void hurry();							// define sending the hurry msg
void enddd();							// define sending the end msg
void gpioInitial();						// define the initial process of GPIO
void ctrlConnectionLight(bool b);		// define the operation of the connection LED
void ctrlHurryLight(bool b);			// define the operation of the hurry LED
int getHurryButtonStatus();				// define getting the hurry button's value
int getEndButtonStatus();				// define getting the end button's value
void BBBMain();							// define the physical main function toward BBB
void FilesClose();						// define the closing process of whole files


/*
	To free the space of the memory, including socket and session.
	Input	=> the SDP session object want to release
*/
void endWholeProgram(sdp_session_t* s){
	close( localSocket );
	sdp_close(s);
}

/*
	To send the hurry message to the phone
*/
void hurry(){
	// fill the msg as hurry
	char _ [5] = "hurry";
	memset(msg_send, 0, sizeof(msg_send) );
	strcat(msg_send, _);

	// send the hurry message
	int writeStatus;
	writeStatus = write( clientSocket, msg_send, sizeof(msg_send) );
	if( writeStatus > 0){
		fprintf(stdout, "send Hurry successful..\n");
	}
}

/*
	To send the end message to the phone
*/
void enddd(){
	// fill the msg as hurry
	char _ [5] = "enddd";
	memset(msg_send, 0, sizeof(msg_send) );
	strcat(msg_send, _);

	// send the hurry message
	int writeStatus;
	writeStatus = write( clientSocket, msg_send, sizeof(msg_send) );
	if( writeStatus > 0){
		fprintf(stdout, "send Enddd successful..\n");
	}
}

/*
	Implement the initialization of GPIO
	hurry light is at GPIO60 	=> P9_12
	connect light is at GPIO48	=> P9_15
	hurry button is at GPIO30	=> P9_11
	end button is at GPIO31		=> P9_13
*/
void gpioInitial(){
	FILE *export_file = NULL;	// file descriptor about export led
	int value, i;

	//assign the gpio number port
	in1 = fopen("/sys/class/gpio/export", "w");
	fprintf(in1,"%d",PIN_HRYBTN);
	fflush(in1);

	in2 = fopen("/sys/class/gpio/export", "w");
	fprintf(in2,"%d",PIN_ENDBTN);
	fflush(in2);

	indir1 = fopen("/sys/class/gpio/gpio30/direction", "w");
	fprintf(indir1,"in");
	fflush(indir1);

	indir2 = fopen("/sys/class/gpio/gpio31/direction", "w");
	fprintf(indir2,"in");
	fflush(indir2);

	//value to pass to export file(connect light)
	char strr1[] = "48";	
	export_file = fopen ("/sys/class/gpio/export", "w");
	fwrite (strr1, 1, sizeof(strr1), export_file);
	fclose (export_file);

	//value to pass to export file(hurry light)
	char strr2[] = "60";	
	export_file = fopen ("/sys/class/gpio/export", "w");
	fwrite (strr2, 1, sizeof(strr2), export_file);
	fclose (export_file);

	FilesClose();
}

/*
	control the connection LED. 
	True --> turn on the light
	False --> trun off the light
	Input	=> the boolean represent the operation
*/
void ctrlConnectionLight(bool b){
	char str1[] = "low";
	char str2[] = "high";

	// open the direction file
	FILE *IO_direction = NULL;
	IO_direction = fopen ("/sys/class/gpio/gpio48/direction", "w");
	if(IO_direction == NULL)
		printf("cannot open the GPIO-48 light file\n");

	// turn on/off check
	if(b == true){
		fwrite (str2, 1, sizeof(str2), IO_direction);   //set the pin to HIGH
		printf("turn on Hurry light.\n");
	}
	else{
		fwrite (str1, 1, sizeof(str1), IO_direction);   //set the pin to LOW
		printf("turn off Hurry light.\n");
	}	
	fclose (IO_direction);
}

/*
	control the connection LED. 
	True --> turn on the light
	False --> trun off the light
	Input	=> the boolean represent the operation
*/
void ctrlHurryLight(bool b){
	char str1[] = "low";
	char str2[] = "high";

	// open the direction file
	FILE *IO_direction = NULL;
	IO_direction = fopen ("/sys/class/gpio/gpio60/direction", "w");
	if(IO_direction == NULL)
		printf("cannot open the GPIO-60 light file\n");

	// turn on/off check
	if(b == true){
		fwrite (str2, 1, sizeof(str2), IO_direction);	//set the pin to HIGH
		printf("turn on Hurry light.\n");
	}
	else{
		fwrite (str1, 1, sizeof(str1), IO_direction);   //set the pin to LOW
		printf("turn off Hurry light.\n");
	}
	fclose (IO_direction);
}

/*
	Get the status of the hurry button.
	Output	=> the boolean status
*/
int getHurryButtonStatus(){
	FILE* inval;
	int value;
	inval = fopen("/sys/class/gpio/gpio30/value", "r");
	fscanf(inval,"%d",&value);
	fclose(inval);
	return value;
}

/*
	Get the status of the hurry button.
	Output	=> the boolean status
*/
int getEndButtonStatus(){
	FILE* inval;
	int value;
	inval = fopen("/sys/class/gpio/gpio31/value", "r");
	fscanf(inval,"%d",&value);
	fclose(inval);
	return value;
}

/*
	Implement the physical main function of BBB
*/
void BBBMain(){
	//initial the GPIO
	gpioInitial();

	//turn on the connection light
	ctrlConnectionLight(true);

	//keep trace if the button press
	while(1){
		int status = 0;

		usleep(1000000);

		//trace the hurry button
		status = getHurryButtonStatus();
		printf("status = %d\n", status);
		if (status == 1){
			ctrlHurryLight(true);
			hurry();
		}

		//trace the end button
		status = getEndButtonStatus();
		if (status == 1){
			ctrlHurryLight(false);
			enddd();	
		}	
	}

	//turn off the connection light
	ctrlConnectionLight(false);

	//close the whole relative file
	FilesClose();
}

/*
	close the button direction and export file.
*/
void FilesClose(){
	fclose(in1);
	fclose(in2);
	fclose(indir1);
	fclose(indir2);
}

int main(){
	// initialize the variable
	pthread_t th_r, th_w;
	struct 	sockaddr_rc locAddr = { 0 }, clientAddr = { 0 };
	char buff[ 1 ] = { 0 };
	sdp_session_t *session;
	socklen_t clientLen = sizeof(clientAddr);

	// socket operation
	localSocket = socket( AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
	locAddr.rc_family = AF_BLUETOOTH;
	locAddr.rc_bdaddr = *BDADDR_ANY;

	// search the available channel
	for(mChannel = 1; mChannel<31; mChannel++){
		int err;
		locAddr.rc_channel = (uint8_t) mChannel;
		err = bind(localSocket, (struct sockaddr* )&locAddr, sizeof(locAddr) );
		if( !err ) break;
	}
	
	// check if the channel is invalid
	if( mChannel >= 31 )
		printf("didn't substitude channel!\n");

	// regist the session
	session = register_service(mChannel);

	// bind the socket
	listen(localSocket, 1);	
	fprintf(stderr, "(Smart Belt) >> listen already.\n");

	while(true){
		fprintf(stderr, "wait for reading connection\n" );
		clientSocket = accept(localSocket, ( struct sockaddr* )&clientAddr, 
						&clientLen);

		fprintf(stderr, "(Smart Belt) >> connect success.\n");
		memset(buff, 0, sizeof(buff) );

		/*
		pthread_create(&th_r, NULL, thread_Read, NULL);
		pthread_create(&th_w, NULL, thread_Write, NULL);

		pthread_join(th_w, NULL );
		fprintf(stderr, "write thread close\n" );

		pthread_cancel(th_r);
		//pthread_join(th_r, NULL );
				
		fprintf(stderr, "client socket close\n" );
		*/

		//send the message
		//usleep(1000000);

		//BBB physical control
		BBBMain();

		close( clientSocket );
	}
	fprintf(stderr, "(Smart Belt) >> program close\n" );		
	endWholeProgram(session);
	return 0;
}