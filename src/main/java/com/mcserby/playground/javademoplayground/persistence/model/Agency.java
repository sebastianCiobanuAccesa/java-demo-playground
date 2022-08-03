package com.mcserby.playground.javademoplayground.persistence.model;


import com.mcserby.playground.javademoplayground.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "agency")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Agency extends MarketActor {

    @Column
    private String cui;

    @OneToMany(mappedBy = "agency", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<ExchangePool> exchangePools;

//    @Transient
//    public Graph<String, ExchangePool> getGraph() {
//        System.out.println("WWWW");
//        if(this.graph == null) {
//            this.graph = new DefaultDirectedGraph<>(ExchangePool.class);
//
//            for(var exchangePool: exchangePools) {
//                String source = exchangePool.getLiquidityOne().getTicker();
//                String destination = exchangePool.getLiquidityTwo().getTicker();
//                this.graph.addVertex(source);
//                this.graph.addVertex(destination);
//
//                this.graph.addEdge(source, destination, (exchangePool));
//                ExchangePool reverseExchangePool = ExchangePool.builder()
//                        .id(1L)
//                        .liquidityOne((exchangePool.getLiquidityTwo()))
//                        .liquidityTwo((exchangePool.getLiquidityOne()))
//                        .build();
//                this.graph.addEdge(destination, source, reverseExchangePool);
//            }
//        }
//
//        return this.graph;
//    }

    @Transient
    private Graph<String, ExchangePool> graph;


    public void setExchangePoolReferences() {
        exchangePools.forEach(e -> e.setAgency(this));
    }

    // CHANGE
    public static abstract class AgencyBuilder<C extends Agency, B extends AgencyBuilder<C, B>>
            extends MarketActorBuilder<C, B> {
        private List<ExchangePool> exchangePools;

        private Graph<String, ExchangePool> graph;

        public static void f () {

        }
        public B exchangePools(List<ExchangePool> exchangePools) {
            System.out.println("exchangePools");
            this.exchangePools = exchangePools;
            this.graph = new DefaultDirectedGraph<>(ExchangePool.class);

            for(var exchangePool: exchangePools) {
                String source = exchangePool.getLiquidityOne().getTicker();
                String destination = exchangePool.getLiquidityTwo().getTicker();
                this.graph.addVertex(source);
                this.graph.addVertex(destination);

                this.graph.addEdge(source, destination, (exchangePool));
                ExchangePool reverseExchangePool = ExchangePool.builder()
                        .id(1L)
                        .liquidityOne((exchangePool.getLiquidityTwo()))
                        .liquidityTwo((exchangePool.getLiquidityOne()))
                        .build();
                this.graph.addEdge(destination, source, reverseExchangePool);
            }

            return self();
        }
    }

}
