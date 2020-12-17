package com.taotao.solrj;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;

public class TestSolrj {
    @Test
    public void addDocument() throws IOException, SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://192.168.186.102:8080/solr");
        SolrInputDocument document = new SolrInputDocument();

        document.addField("id", "test012");
        document.addField("item_title","这是一个测试");
        solrServer.add(document);
        solrServer.commit();
    }

    @Test
    public void testSolrCloud() throws IOException, SolrServerException {
        CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.186.102:2181,192.168.186.103:2181,192.168.186.104:2181");
        cloudSolrServer.setDefaultCollection("collection2");
        SolrInputDocument document = new SolrInputDocument();

        document.addField("id", "testSolrCloud");
        document.addField("item_title","哈哈哈");

        cloudSolrServer.add(document);
        cloudSolrServer.commit();
    }

    @Test
    public void testQuery() throws SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://192.168.186.102:8080/solr");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("阿尔卡特");
        solrQuery.addFilterQuery("item_price:[0 TO 300000]");
        solrQuery.set("df", "item_title");

        QueryResponse response = solrServer.query(solrQuery);
        SolrDocumentList results = response.getResults();
        System.out.println("查询总记录" + results.getNumFound());
        for (SolrDocument solrDocument : results) {
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("item_title"));
        }
    }
}
