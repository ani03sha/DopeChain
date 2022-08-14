package org.redquark.web3.services;

import org.redquark.web3.entities.blocks.Block;

import java.util.List;

public interface ChainValidatorService {

    boolean isChainValid(List<Block> blockchain);
}
