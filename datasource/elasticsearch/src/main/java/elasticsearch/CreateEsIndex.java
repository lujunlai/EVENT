package elasticsearch;

import java.net.InetAddress;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class CreateEsIndex {
	
	public static void createMapping(String indices,String mappingType)throws Exception{
		new XContentFactory();
		
		XContentBuilder builder=XContentFactory.jsonBuilder()
		.startObject()
			.startObject("properties")
				.startObject("eventType").field("type", "string").field("index","not_analyzed").endObject()
				.startObject("location").field("type", "string").field("index","not_analyzed").endObject()
				.startObject("eventTime").field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss").endObject()
				.startObject("resourceInfo")
					.startObject("properties")
						.startObject("title").field("type", "string").field("analyzer", "ik").endObject()
						.startObject("raw").field("type", "string").field("analyzer", "ik").endObject()
					.endObject()
				.endObject()
				.startObject("entities")
					.startObject("properties")
						.startObject("涉及产品").field("type", "string").field("index", "not_analyzed").endObject()
						.startObject("涉及品类").field("type", "string").field("index", "not_analyzed").endObject()
						.startObject("风险").field("type", "integer").endObject()
						.startObject("原因").field("type", "string").field("index", "not_analyzed").endObject()
						.startObject("通报国").field("type", "string").field("index", "not_analyzed").endObject()
						.startObject("原产国").field("type", "string").field("index", "not_analyzed").endObject()
						.startObject("涉及生产商").field("type", "string").field("index", "not_analyzed").endObject()
						.startObject("涉及品牌").field("type", "string").field("index", "not_analyzed").endObject()
					.endObject()
				.endObject()
			.endObject()
		.endObject();
		PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
		Settings settings = Settings.settingsBuilder()
	            .put("cluster.name","testcluster")
	            .put("client.transport.sniff", true)
	            .build();
		TransportClient client = TransportClient.builder().settings(settings).build();
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.253"), 9300));
		client.admin().indices().putMapping(mapping).actionGet();
		client.close();
	}
	
	public static void main(String[] args)throws Exception {
		Settings settings = Settings.settingsBuilder()
	            .put("cluster.name","testcluster")
	            .put("client.transport.sniff", true)
	            .build();
		TransportClient client = TransportClient.builder().settings(settings).build();
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.253"), 9300));
		CreateIndexRequest request = new CreateIndexRequest("event_index_qualityrisk");
		client.admin().indices().create(request);
		client.close();
		String[] types = {"event_type_qualityrisk"};
		for(String type : types) {
			createMapping("event_index_qualityrisk", type);
		}
		System.out.println("build success...........");
	}
}

