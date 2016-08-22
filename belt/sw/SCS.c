#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include "/usr/include/bluetooth/bluetooth.h"
#include "/usr/include/bluetooth/rfcomm.h"
#include "/usr/include/bluetooth/sdp.h"
#include "/usr/include/bluetooth/sdp_lib.h"

/* 	
	Implement the SDP function
	The UUID: 00001101-0000-1000-8000-00805F9B34FB
	Output	=> the SDP session object
	<<Notice>>:	the subroutine would use "mChannel" global variable
*/
sdp_session_t *register_service(int mChannel){
	uint8_t serviceUUid_int[] = {	0x00, 0x00, 0x11, 0x01,
									0x00, 0x00, 0x10, 0x00,
									0x80, 0x00, 0x00, 0x80,
									0x5F, 0x9B, 0x34, 0xFB	};
	uint8_t rfcommChannel = mChannel;
	uuid_t rootUUid, l2capUUid, rfcommUUid, serviceUUid;

	sdp_list_t 	*l2capList = 0, *rfcommList = 0,
				*rootList = 0, *protoList = 0, 
				*accessProtoList = 0, *classIdList;
	sdp_data_t  *channel = 0, *psm = 0;

	sdp_record_t *record = sdp_record_alloc();

	sdp_uuid128_create(&serviceUUid, &serviceUUid_int);
	sdp_set_service_id( record, serviceUUid );

	sdp_uuid16_create(&rootUUid, PUBLIC_BROWSE_GROUP);
	rootList = sdp_list_append(0, &rootUUid);
	sdp_set_browse_groups(record, rootList);

	//assign l2cap information
	sdp_uuid16_create(&l2capUUid, L2CAP_UUID);
	l2capList = sdp_list_append(0, &l2capUUid);
	protoList = sdp_list_append(0, l2capList);

	//assign rfcomm information
	sdp_uuid16_create(&rfcommUUid, RFCOMM_UUID);
	channel = sdp_data_alloc(SDP_UINT8, &rfcommChannel);
	rfcommList = sdp_list_append(0, &rfcommUUid);
	sdp_list_append(rfcommList, channel);
	sdp_list_append(protoList, rfcommList);

	//add protocol information to service record
	accessProtoList = sdp_list_append(0, protoList);
	sdp_set_access_protos( record, accessProtoList);

	//service class(for android)
	classIdList = sdp_list_append(0, &serviceUUid);
	sdp_set_service_classes( record, classIdList);

	//set other information(skip here)
	sdp_set_info_attr(record, "", "", "");

	// connect to the local SDP server, register the service record
	sdp_session_t *session = 0;
	int error_return = 0;

	session = sdp_connect( BDADDR_ANY, BDADDR_LOCAL, SDP_RETRY_IF_BUSY );
	error_return = sdp_record_register(session, record, 0);

	if( error_return!= 0 )
		perror("fail to register service record");

	//disallocate the data and list
	sdp_data_free( channel );
	sdp_list_free( l2capList, 0 );
	sdp_list_free( rfcommList, 0 );
	sdp_list_free( rootList, 0 );
	sdp_list_free( accessProtoList, 0);

	return session;
}