package org.raku.comma.cro.template.parsing;
import org.raku.comma.parsing.Cursor;

public class MAINBraid extends Cursor<MAINBraid> {

    @Override
    public int runRule() {
        return switch (this.ruleNumber) {
            case 0 -> this._0_TOP();
            case 1 -> this._1_bogus_end();
            case 2 -> this._2_sequence_element();
            case 3 -> this._3_sequence_element_group();
            case 4 -> this._4_comment_tag();
            case 5 -> this._5_sequence_element_literal_text();
            case 6 -> this._6_sequence_element_literal_open_tag();
            case 7 -> this._7_sequence_element_literal_close_tag();
            case 8 -> this._8_literal_tag_name();
            case 9 -> this._9_literal_tag_attribute();
            case 10 -> this._10_literal_tag_attribute_value();
            case 11 -> this._11_sigil();
            case 12 -> this._12_sigil_tag();
            case 13 -> this._13_sigil_tag_topic();
            case 14 -> this._14_sigil_tag_variable();
            case 15 -> this._15_sigil_tag_iteration();
            case 16 -> this._16_sigil_tag_condition();
            case 17 -> this._17_elsif();
            case 18 -> this._18_else();
            case 19 -> this._19_structural_tag();
            case 20 -> this._20_block();
            case 21 -> this._21_sigil_tag_call();
            case 22 -> this._22_sigil_tag_sub();
            case 23 -> this._23_sigil_tag_macro();
            case 24 -> this._24_sigil_tag_body();
            case 25 -> this._25_sigil_tag_part();
            case 26 -> this._26_sigil_tag_use();
            case 27 -> this._27_sigil_tag_separator();
            case 28 -> this._28_sigil_tag_comment();
            case 29 -> this._29_module_name();
            case 30 -> this._30_sigil_tag_apply();
            case 31 -> this._31_signature();
            case 32 -> this._32_parameter();
            case 33 -> this._33_parameter_name();
            case 34 -> this._34_arglist();
            case 35 -> this._35_argument();
            case 36 -> this._36_expression();
            case 37 -> this._37_nextterm();
            case 38 -> this._38_infixish();
            case 39 -> this._39_infix();
            case 40 -> this._40_term();
            case 41 -> this._41_single_quote_string();
            case 42 -> this._42_int();
            case 43 -> this._43_rat();
            case 44 -> this._44_num();
            case 45 -> this._45_bool();
            case 46 -> this._46_variable();
            case 47 -> this._47_deref_term();
            case 48 -> this._48_parenthesized_expression();
            case 49 -> this._49_deref();
            case 50 -> this._50_deref_item();
            case 51 -> this._51_deref_item_method();
            case 52 -> this._52_deref_item_smart();
            case 53 -> this._53_deref_item_hash_literal();
            case 54 -> this._54_deref_item_array();
            case 55 -> this._55_deref_item_hash();
            case 56 -> this._56_ws();
            case 57 -> this._57_hws();
            case 58 -> this._58_tlt();
            case 59 -> this._59_tgt();
            case 60 -> this._60_tclose();
            case 61 -> this._61_dot();
            case 62 -> this._62_lt();
            case 63 -> this._63_gt();
            case 64 -> this._64_close();
            case 65 -> this._65_iter_sigil();
            case 66 -> this._66_cond_sigil();
            case 67 -> this._67_call_sigil();
            case 68 -> this._68_decl_sigil();
            case 69 -> this._69_apply_sigil();
            case 70 -> this._70_identifier();
            case 71 -> this._71_ident();
            case 72 -> this.___lookahead_0();
            case 73 -> this.___lookahead_1();
            case 74 -> this.___lookahead_2();
            case 75 -> this.___lookahead_3();
            case 76 -> this.___lookahead_4();
            case 77 -> this.___lookahead_5();
            case 78 -> this.___lookahead_6();
            case 79 -> this.___lookahead_7();
            case 80 -> this.___lookahead_8();
            case 81 -> this.___lookahead_9();
            case 82 -> this.___lookahead_10();
            case 83 -> this.___lookahead_11();
            case 84 -> this.___lookahead_12();
            case 85 -> this.___lookahead_13();
            case 86 -> this.___lookahead_14();
            case 87 -> this.___lookahead_15();
            case 88 -> this.___lookahead_16();
            case 89 -> this.___lookahead_17();
            case 90 -> this.___lookahead_18();
            case 91 -> this.___lookahead_19();
            case 92 -> this.___lookahead_20();
            case 93 -> this.___lookahead_21();
            case 94 -> this.___lookahead_22();
            case 95 -> this.___lookahead_23();
            case 96 -> this.___lookahead_24();
            case 97 -> this.___lookahead_25();
            case 98 -> this.___lookahead_26();
            case 99 -> this.___lookahead_27();
            case 100 -> this.___lookahead_28();
            case 101 -> this.___lookahead_29();
            case 102 -> this.___lookahead_30();
            case 103 -> this.___lookahead_31();
            case 104 -> this.___lookahead_32();
            case 105 -> this.___lookahead_33();
            case 106 -> this.___lookahead_34();
            case 107 -> this.___lookahead_35();
            case 108 -> this.___lookahead_36();
            case 109 -> this.___lookahead_37();
            case 110 -> this.___lookahead_38();
            case 111 -> this.___lookahead_39();
            default -> throw new RuntimeException();
        };
    }

    private int _0_TOP() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsMark(3);
                this.state = 1;
                break;
            case 1:
                this.setArgs();
                this.state = 2;
                return 2;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                rep = this.peekRep(3);
                ++rep;
                this.bsCommit(3);
                this.bsMark(3, rep);
                this.state = 1;
                continue;

            case 3:
                this.bsMark(6);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 1;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(6);
                this.state = 6;
                continue;

            case 6:
                return -1;

            }
        }
    }

    private int _1_bogus_end() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.BAD_CHARACTER);
                this.bsFailMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.anyChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(2);
                ++rep;
                this.bsCommit(2);
                this.bsMark(2, rep);
                this.state = 1;
                continue;

            case 2:
                this.state = 3;
                return -3;

            case 3:
                return -1;

            }
        }
    }

    private int _2_sequence_element() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(10);
                this.bsMark(2);
                this.setArgs();
                this.state = 1;
                return 4;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 2:
                this.bsMark(4);
                this.setArgs();
                this.state = 3;
                return 7;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 4:
                this.bsMark(6);
                this.setArgs();
                this.state = 5;
                return 6;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 6:
                this.bsMark(8);
                this.setArgs();
                this.state = 7;
                return 5;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 8:
                this.setArgs();
                this.state = 9;
                return 12;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 10;
                continue;

            case 10:
                return -1;

            }
        }
    }

    private int _3_sequence_element_group() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsMark(3);
                this.state = 1;
                break;
            case 1:
                this.setArgs();
                this.state = 2;
                return 2;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                rep = this.peekRep(3);
                ++rep;
                this.bsCommit(3);
                this.bsMark(3, rep);
                this.state = 1;
                continue;

            case 3:
                return -1;

            }
        }
    }

    private int _4_comment_tag() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.HTML_COMMENT);
                if (!(this.literal("<!--"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(9);
                this.state = 1;
                break;
            case 1:
                this.bsFailMark(8);
                this.bsMark(4);
                this.bsFailMark(3);
                this.state = 2;
                break;
            case 2:
                if (!(this.wordChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(3);
                ++rep;
                this.bsCommit(3);
                this.bsMark(3, rep);
                this.state = 2;
                continue;

            case 3:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 4:
                this.bsMark(7);
                this.bsFailMark(6);
                this.state = 5;
                break;
            case 5:
                if (!(this.spaceChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(6);
                ++rep;
                this.bsCommit(6);
                this.bsMark(6, rep);
                this.state = 5;
                continue;

            case 6:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 7:
                if (this.lookahead(72)) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                if (!(this.anyChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 8;
                continue;

            case 8:
                rep = this.peekRep(9);
                ++rep;
                this.bsCommit(9);
                this.bsMark(9, rep);
                this.state = 1;
                continue;

            case 9:
                this.bsMark(11);
                this.state = 10;
                break;
            case 10:
                if (!(this.literal("-->"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.state = 12;
                return -3;

            case 12:
                return -1;

            }
        }
    }

    private int _5_sequence_element_literal_text() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.LITERAL_TEXT);
                this.bsFailMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.notInCharList("<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(2);
                ++rep;
                this.bsCommit(2);
                this.bsMark(2, rep);
                this.state = 1;
                continue;

            case 2:
                this.state = 3;
                return -3;

            case 3:
                return -1;

            }
        }
    }

    private int _6_sequence_element_literal_open_tag() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(73))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 62;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(16);
                this.state = 2;
                break;
            case 2:
                this.setArgs();
                this.state = 3;
                return 8;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 4;
                return 56;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(12);
                this.state = 5;
                break;
            case 5:
                this.bsFailMark(11);
                this.bsMark(8);
                this.setArgs();
                this.state = 6;
                return 9;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 7;
                return 56;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 8:
                this.setArgs();
                this.state = 9;
                return 12;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 10;
                return 56;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 11;
                continue;

            case 11:
                rep = this.peekRep(12);
                ++rep;
                this.bsCommit(12);
                this.bsMark(12, rep);
                this.state = 5;
                continue;

            case 12:
                this.bsMark(15);
                this.state = 13;
                break;
            case 13:
                this.setArgs();
                this.state = 14;
                return 63;

            case 14:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(15);
                this.state = 15;
                continue;

            case 15:
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 16:
                return -1;

            }
        }
    }

    private int _7_sequence_element_literal_close_tag() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(76))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 62;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 64;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(11);
                this.state = 3;
                break;
            case 3:
                this.setArgs();
                this.state = 4;
                return 56;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(10);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 8;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(9);
                this.state = 7;
                break;
            case 7:
                this.setArgs();
                this.state = 8;
                return 63;

            case 8:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 10:
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                return -1;

            }
        }
    }

    private int _8_literal_tag_name() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.LITERAL_TAG_NAME);
                this.setArgs();
                this.state = 1;
                return 71;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 2;
                return -3;

            case 2:
                return -1;

            }
        }
    }

    private int _9_literal_tag_attribute() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_NAME);
                this.setArgs();
                this.state = 1;
                return 70;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 2;
                return -3;

            case 2:
                this.setArgs();
                this.state = 3;
                return 56;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(10);
                this.state = 4;
                break;
            case 4:
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_EQUALS);
                if (!(this.literal("="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 5;
                return -3;

            case 5:
                this.setArgs();
                this.state = 6;
                return 56;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(9);
                this.state = 7;
                break;
            case 7:
                this.setArgs();
                this.state = 8;
                return 10;

            case 8:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 10:
                return -1;

            }
        }
    }

    private int _10_literal_tag_attribute_value() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(34);
                this.bsMark(15);
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_QUOTE);
                if (!(this.literal("\""))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.bsMark(11);
                this.state = 2;
                break;
            case 2:
                this.bsFailMark(10);
                this.bsMark(6);
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_VALUE);
                this.bsFailMark(4);
                this.state = 3;
                break;
            case 3:
                if (!(this.notInCharList("\"<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 3;
                continue;

            case 4:
                this.state = 5;
                return -3;

            case 5:
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 6:
                this.bsMark(8);
                this.setArgs();
                this.state = 7;
                return 12;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 8:
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_VALUE);
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 9;
                return -3;

            case 9:
                this.state = 10;
                continue;

            case 10:
                rep = this.peekRep(11);
                ++rep;
                this.bsCommit(11);
                this.bsMark(11, rep);
                this.state = 2;
                continue;

            case 11:
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_QUOTE);
                if (!(this.literal("\""))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 13;
                return -3;

            case 13:
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsCommit(34);
                this.state = 34;
                continue;

            case 15:
                this.bsMark(30);
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_QUOTE);
                if (!(this.literal("'"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 16;
                return -3;

            case 16:
                this.bsMark(26);
                this.state = 17;
                break;
            case 17:
                this.bsFailMark(25);
                this.bsMark(21);
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_VALUE);
                this.bsFailMark(19);
                this.state = 18;
                break;
            case 18:
                if (!(this.notInCharList("'<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(19);
                ++rep;
                this.bsCommit(19);
                this.bsMark(19, rep);
                this.state = 18;
                continue;

            case 19:
                this.state = 20;
                return -3;

            case 20:
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 21:
                this.bsMark(23);
                this.setArgs();
                this.state = 22;
                return 12;

            case 22:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 23:
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_VALUE);
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 24;
                return -3;

            case 24:
                this.state = 25;
                continue;

            case 25:
                rep = this.peekRep(26);
                ++rep;
                this.bsCommit(26);
                this.bsMark(26, rep);
                this.state = 17;
                continue;

            case 26:
                this.bsMark(29);
                this.state = 27;
                break;
            case 27:
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_QUOTE);
                if (!(this.literal("'"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 28;
                return -3;

            case 28:
                this.bsCommit(29);
                this.state = 29;
                continue;

            case 29:
                this.bsCommit(34);
                this.state = 34;
                continue;

            case 30:
                this.startToken(CroTemplateTokenTypes.ATTRIBUTE_VALUE);
                this.bsMark(32);
                this.state = 31;
                break;
            case 31:
                if (!(this.notSpaceChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(32);
                ++rep;
                this.bsCommit(32);
                this.bsMark(32, rep);
                this.state = 31;
                continue;

            case 32:
                this.state = 33;
                return -3;

            case 33:
                this.state = 34;
                continue;

            case 34:
                return -1;

            }
        }
    }

    private int _11_sigil() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(3);
                this.bsMark(1);
                if (!(this.inCharList(".$@&:|"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(3);
                this.state = 3;
                continue;

            case 1:
                this.bsMark(2);
                if (!(this.inCharList("?!"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                if (!(this.inCharList(".$>{"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(3);
                this.state = 3;
                continue;

            case 2:
                if (!(this.literal("#>"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                continue;

            case 3:
                return -1;

            }
        }
    }

    private int _12_sigil_tag() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(26);
                this.bsMark(2);
                this.setArgs();
                this.state = 1;
                return 13;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 2:
                this.bsMark(4);
                this.setArgs();
                this.state = 3;
                return 14;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 4:
                this.bsMark(6);
                this.setArgs();
                this.state = 5;
                return 15;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 6:
                this.bsMark(8);
                this.setArgs();
                this.state = 7;
                return 16;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 8:
                this.bsMark(10);
                this.setArgs();
                this.state = 9;
                return 21;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 10:
                this.bsMark(12);
                this.setArgs();
                this.state = 11;
                return 22;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 12:
                this.bsMark(14);
                this.setArgs();
                this.state = 13;
                return 23;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 14:
                this.bsMark(16);
                this.setArgs();
                this.state = 15;
                return 24;

            case 15:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 16:
                this.bsMark(18);
                this.setArgs();
                this.state = 17;
                return 30;

            case 17:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 18:
                this.bsMark(20);
                this.setArgs();
                this.state = 19;
                return 25;

            case 19:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 20:
                this.bsMark(22);
                this.setArgs();
                this.state = 21;
                return 27;

            case 21:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 22:
                this.bsMark(24);
                this.setArgs();
                this.state = 23;
                return 26;

            case 23:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 24:
                this.setArgs();
                this.state = 25;
                return 28;

            case 25:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 26;
                continue;

            case 26:
                return -1;

            }
        }
    }

    private int _13_sigil_tag_topic() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(78))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 61;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(8);
                this.state = 3;
                break;
            case 3:
                this.setArgs();
                this.state = 4;
                return 49;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(7);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 59;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                return -1;

            }
        }
    }

    private int _14_sigil_tag_variable() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(79))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.VARIABLE_NAME);
                if (!(this.literal("$"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(4);
                this.state = 2;
                break;
            case 2:
                this.setArgs();
                this.state = 3;
                return 70;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(4);
                this.state = 4;
                continue;

            case 4:
                this.state = 5;
                return -3;

            case 5:
                this.bsMark(11);
                this.state = 6;
                break;
            case 6:
                this.setArgs();
                this.state = 7;
                return 61;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(10);
                this.state = 8;
                break;
            case 8:
                this.setArgs();
                this.state = 9;
                return 49;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 10:
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 59;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                return -1;

            }
        }
    }

    private int _15_sigil_tag_iteration() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(80))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 65;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(52);
                this.state = 3;
                break;
            case 3:
                this.bsFailMark(18);
                this.bsMark(8);
                this.setArgs();
                this.state = 4;
                return 61;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(7);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 49;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 8:
                this.bsMark(10);
                this.setArgs();
                this.state = 9;
                return 49;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 10:
                this.setArgs();
                this.state = 11;
                return 46;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(17);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 61;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(16);
                this.state = 14;
                break;
            case 14:
                this.setArgs();
                this.state = 15;
                return 49;

            case 15:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 16:
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.state = 18;
                continue;

            case 18:
                this.bsMark(33);
                this.state = 19;
                break;
            case 19:
                if (!(this.lookahead(81))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(22);
                this.state = 20;
                break;
            case 20:
                this.setArgs();
                this.state = 21;
                return 57;

            case 21:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(22);
                this.state = 22;
                continue;

            case 22:
                this.startToken(CroTemplateTokenTypes.COLON);
                if (!(this.literal(":"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 23;
                return -3;

            case 23:
                this.bsMark(32);
                this.state = 24;
                break;
            case 24:
                if (!(this.lookahead(82))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(27);
                this.state = 25;
                break;
            case 25:
                this.setArgs();
                this.state = 26;
                return 57;

            case 26:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(27);
                this.state = 27;
                continue;

            case 27:
                this.startToken(CroTemplateTokenTypes.VARIABLE_NAME);
                if (!(this.literal("$"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(30);
                this.state = 28;
                break;
            case 28:
                this.setArgs();
                this.state = 29;
                return 70;

            case 29:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(30);
                this.state = 30;
                continue;

            case 30:
                this.state = 31;
                return -3;

            case 31:
                this.bsCommit(32);
                this.state = 32;
                continue;

            case 32:
                this.bsCommit(33);
                this.state = 33;
                continue;

            case 33:
                this.bsMark(39);
                this.state = 34;
                break;
            case 34:
                this.setArgs();
                this.state = 35;
                return 57;

            case 35:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(38);
                this.state = 36;
                break;
            case 36:
                this.setArgs();
                this.state = 37;
                return 19;

            case 37:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(38);
                this.state = 38;
                continue;

            case 38:
                this.bsCommit(39);
                this.state = 39;
                continue;

            case 39:
                this.bsMark(51);
                this.state = 40;
                break;
            case 40:
                this.setArgs();
                this.state = 41;
                return 59;

            case 41:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 42;
                return 3;

            case 42:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(50);
                this.state = 43;
                break;
            case 43:
                if (!(this.lookahead(83))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 44;
                return 58;

            case 44:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 45;
                return 60;

            case 45:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 46;
                return 65;

            case 46:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(49);
                this.state = 47;
                break;
            case 47:
                this.setArgs();
                this.state = 48;
                return 59;

            case 48:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(49);
                this.state = 49;
                continue;

            case 49:
                this.bsCommit(50);
                this.state = 50;
                continue;

            case 50:
                this.bsCommit(51);
                this.state = 51;
                continue;

            case 51:
                this.bsCommit(52);
                this.state = 52;
                continue;

            case 52:
                return -1;

            }
        }
    }

    private int _16_sigil_tag_condition() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(84))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 66;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(39);
                this.state = 3;
                break;
            case 3:
                this.bsFailMark(12);
                this.bsMark(8);
                this.setArgs();
                this.state = 4;
                return 61;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(7);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 49;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                this.bsCommit(12);
                this.state = 12;
                continue;

            case 8:
                this.bsMark(10);
                this.setArgs();
                this.state = 9;
                return 20;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(12);
                this.state = 12;
                continue;

            case 10:
                this.setArgs();
                this.state = 11;
                return 46;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 12;
                continue;

            case 12:
                this.bsMark(38);
                this.state = 13;
                break;
            case 13:
                this.bsMark(19);
                this.state = 14;
                break;
            case 14:
                this.setArgs();
                this.state = 15;
                return 57;

            case 15:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(18);
                this.state = 16;
                break;
            case 16:
                this.setArgs();
                this.state = 17;
                return 19;

            case 17:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 18:
                this.bsCommit(19);
                this.state = 19;
                continue;

            case 19:
                this.setArgs();
                this.state = 20;
                return 59;

            case 20:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 21;
                return 3;

            case 21:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(37);
                this.state = 22;
                break;
            case 22:
                if (!(this.lookahead(85))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 23;
                return 58;

            case 23:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 24;
                return 60;

            case 24:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 25;
                return 66;

            case 25:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(36);
                this.state = 26;
                break;
            case 26:
                this.setArgs();
                this.state = 27;
                return 59;

            case 27:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(31);
                this.state = 28;
                break;
            case 28:
                if (!(this.lookahead(86))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 29;
                return 56;

            case 29:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 30;
                return 17;

            case 30:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                rep = this.peekRep(31);
                ++rep;
                this.bsCommit(31);
                this.bsMark(31, rep);
                this.state = 28;
                continue;

            case 31:
                this.bsMark(35);
                this.state = 32;
                break;
            case 32:
                if (!(this.lookahead(87))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 33;
                return 56;

            case 33:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 34;
                return 18;

            case 34:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(35);
                this.state = 35;
                continue;

            case 35:
                this.bsCommit(36);
                this.state = 36;
                continue;

            case 36:
                this.bsCommit(37);
                this.state = 37;
                continue;

            case 37:
                this.bsCommit(38);
                this.state = 38;
                continue;

            case 38:
                this.bsCommit(39);
                this.state = 39;
                continue;

            case 39:
                return -1;

            }
        }
    }

    private int _17_elsif() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(88))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL);
                if (!(this.literal("!?"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 2;
                return -3;

            case 2:
                this.bsMark(31);
                this.state = 3;
                break;
            case 3:
                this.bsFailMark(12);
                this.bsMark(8);
                this.setArgs();
                this.state = 4;
                return 61;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(7);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 49;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                this.bsCommit(12);
                this.state = 12;
                continue;

            case 8:
                this.bsMark(10);
                this.setArgs();
                this.state = 9;
                return 20;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(12);
                this.state = 12;
                continue;

            case 10:
                this.setArgs();
                this.state = 11;
                return 46;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 12;
                continue;

            case 12:
                this.bsMark(30);
                this.state = 13;
                break;
            case 13:
                this.bsMark(19);
                this.state = 14;
                break;
            case 14:
                this.setArgs();
                this.state = 15;
                return 57;

            case 15:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(18);
                this.state = 16;
                break;
            case 16:
                this.setArgs();
                this.state = 17;
                return 19;

            case 17:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 18:
                this.bsCommit(19);
                this.state = 19;
                continue;

            case 19:
                this.setArgs();
                this.state = 20;
                return 59;

            case 20:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 21;
                return 3;

            case 21:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(29);
                this.state = 22;
                break;
            case 22:
                if (!(this.lookahead(89))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 23;
                return 58;

            case 23:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 24;
                return 60;

            case 24:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL);
                if (!(this.literal("?"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 25;
                return -3;

            case 25:
                this.bsMark(28);
                this.state = 26;
                break;
            case 26:
                this.setArgs();
                this.state = 27;
                return 59;

            case 27:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(28);
                this.state = 28;
                continue;

            case 28:
                this.bsCommit(29);
                this.state = 29;
                continue;

            case 29:
                this.bsCommit(30);
                this.state = 30;
                continue;

            case 30:
                this.bsCommit(31);
                this.state = 31;
                continue;

            case 31:
                return -1;

            }
        }
    }

    private int _18_else() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(90))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_ELSE_SIGIL);
                if (!(this.literal("!"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 2;
                return -3;

            case 2:
                this.bsMark(20);
                this.state = 3;
                break;
            case 3:
                this.bsMark(9);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 57;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(8);
                this.state = 6;
                break;
            case 6:
                this.setArgs();
                this.state = 7;
                return 19;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                this.setArgs();
                this.state = 10;
                return 59;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 11;
                return 3;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(19);
                this.state = 12;
                break;
            case 12:
                if (!(this.lookahead(91))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 13;
                return 58;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 14;
                return 60;

            case 14:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_ELSE_SIGIL);
                if (!(this.literal("!"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 15;
                return -3;

            case 15:
                this.bsMark(18);
                this.state = 16;
                break;
            case 16:
                this.setArgs();
                this.state = 17;
                return 59;

            case 17:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 18:
                this.bsCommit(19);
                this.state = 19;
                continue;

            case 19:
                this.bsCommit(20);
                this.state = 20;
                continue;

            case 20:
                return -1;

            }
        }
    }

    private int _19_structural_tag() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.setArgs();
                this.state = 1;
                return 8;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 56;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(10);
                this.state = 3;
                break;
            case 3:
                this.bsFailMark(9);
                this.bsMark(6);
                this.setArgs();
                this.state = 4;
                return 9;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 5;
                return 56;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 6:
                this.setArgs();
                this.state = 7;
                return 12;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 8;
                return 56;

            case 8:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 9;
                continue;

            case 9:
                rep = this.peekRep(10);
                ++rep;
                this.bsCommit(10);
                this.bsMark(10, rep);
                this.state = 3;
                continue;

            case 10:
                return -1;

            }
        }
    }

    private int _20_block() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.OPEN_CURLY);
                if (!(this.literal("{"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.setArgs();
                this.state = 2;
                return 56;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(9);
                this.state = 3;
                break;
            case 3:
                this.setArgs();
                this.state = 4;
                return 36;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 5;
                return 56;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(8);
                this.state = 6;
                break;
            case 6:
                this.startToken(CroTemplateTokenTypes.CLOSE_CURLY);
                if (!(this.literal("}"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 7;
                return -3;

            case 7:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                return -1;

            }
        }
    }

    private int _21_sigil_tag_call() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(92))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 67;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(18);
                this.state = 3;
                break;
            case 3:
                this.startToken(CroTemplateTokenTypes.SUB_NAME);
                this.setArgs();
                this.state = 4;
                return 70;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 5;
                return -3;

            case 5:
                this.bsMark(8);
                this.state = 6;
                break;
            case 6:
                this.setArgs();
                this.state = 7;
                return 57;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsMark(11);
                this.state = 9;
                break;
            case 9:
                this.setArgs();
                this.state = 10;
                return 34;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 57;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsMark(17);
                this.state = 15;
                break;
            case 15:
                this.setArgs();
                this.state = 16;
                return 59;

            case 16:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 18:
                return -1;

            }
        }
    }

    private int _22_sigil_tag_sub() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(93))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 68;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("sub"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                return -3;

            case 3:
                this.bsMark(34);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 57;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(33);
                this.state = 6;
                break;
            case 6:
                this.startToken(CroTemplateTokenTypes.SUB_NAME);
                this.setArgs();
                this.state = 7;
                return 70;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 8;
                return -3;

            case 8:
                this.bsMark(11);
                this.state = 9;
                break;
            case 9:
                this.setArgs();
                this.state = 10;
                return 57;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 31;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsMark(17);
                this.state = 15;
                break;
            case 15:
                this.setArgs();
                this.state = 16;
                return 57;

            case 16:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsMark(32);
                this.state = 18;
                break;
            case 18:
                this.setArgs();
                this.state = 19;
                return 59;

            case 19:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 20;
                return 3;

            case 20:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(31);
                this.state = 21;
                break;
            case 21:
                if (!(this.lookahead(94))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 22;
                return 58;

            case 22:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 23;
                return 60;

            case 23:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 24;
                return 68;

            case 24:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(27);
                this.state = 25;
                break;
            case 25:
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("sub"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 26;
                return -3;

            case 26:
                this.bsCommit(27);
                this.state = 27;
                continue;

            case 27:
                this.bsMark(30);
                this.state = 28;
                break;
            case 28:
                this.setArgs();
                this.state = 29;
                return 59;

            case 29:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(30);
                this.state = 30;
                continue;

            case 30:
                this.bsCommit(31);
                this.state = 31;
                continue;

            case 31:
                this.bsCommit(32);
                this.state = 32;
                continue;

            case 32:
                this.bsCommit(33);
                this.state = 33;
                continue;

            case 33:
                this.bsCommit(34);
                this.state = 34;
                continue;

            case 34:
                return -1;

            }
        }
    }

    private int _23_sigil_tag_macro() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(95))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 68;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("macro"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                return -3;

            case 3:
                this.bsMark(34);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 57;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(33);
                this.state = 6;
                break;
            case 6:
                this.startToken(CroTemplateTokenTypes.MACRO_NAME);
                this.setArgs();
                this.state = 7;
                return 70;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 8;
                return -3;

            case 8:
                this.bsMark(11);
                this.state = 9;
                break;
            case 9:
                this.setArgs();
                this.state = 10;
                return 57;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 31;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsMark(17);
                this.state = 15;
                break;
            case 15:
                this.setArgs();
                this.state = 16;
                return 57;

            case 16:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsMark(32);
                this.state = 18;
                break;
            case 18:
                this.setArgs();
                this.state = 19;
                return 59;

            case 19:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 20;
                return 3;

            case 20:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(31);
                this.state = 21;
                break;
            case 21:
                if (!(this.lookahead(96))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 22;
                return 58;

            case 22:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 23;
                return 60;

            case 23:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 24;
                return 68;

            case 24:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(27);
                this.state = 25;
                break;
            case 25:
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("macro"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 26;
                return -3;

            case 26:
                this.bsCommit(27);
                this.state = 27;
                continue;

            case 27:
                this.bsMark(30);
                this.state = 28;
                break;
            case 28:
                this.setArgs();
                this.state = 29;
                return 59;

            case 29:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(30);
                this.state = 30;
                continue;

            case 30:
                this.bsCommit(31);
                this.state = 31;
                continue;

            case 31:
                this.bsCommit(32);
                this.state = 32;
                continue;

            case 32:
                this.bsCommit(33);
                this.state = 33;
                continue;

            case 33:
                this.bsCommit(34);
                this.state = 34;
                continue;

            case 34:
                return -1;

            }
        }
    }

    private int _24_sigil_tag_body() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(97))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 68;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("body"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                return -3;

            case 3:
                this.bsMark(9);
                this.state = 4;
                break;
            case 4:
                this.bsMark(7);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 57;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                this.setArgs();
                this.state = 8;
                return 59;

            case 8:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                return -1;

            }
        }
    }

    private int _25_sigil_tag_part() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(98))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 68;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("part"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                return -3;

            case 3:
                this.bsMark(34);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 57;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(33);
                this.state = 6;
                break;
            case 6:
                this.startToken(CroTemplateTokenTypes.PART_NAME);
                this.setArgs();
                this.state = 7;
                return 70;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 8;
                return -3;

            case 8:
                this.bsMark(11);
                this.state = 9;
                break;
            case 9:
                this.setArgs();
                this.state = 10;
                return 57;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 31;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsMark(17);
                this.state = 15;
                break;
            case 15:
                this.setArgs();
                this.state = 16;
                return 57;

            case 16:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsMark(32);
                this.state = 18;
                break;
            case 18:
                this.setArgs();
                this.state = 19;
                return 59;

            case 19:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 20;
                return 3;

            case 20:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(31);
                this.state = 21;
                break;
            case 21:
                if (!(this.lookahead(99))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 22;
                return 58;

            case 22:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 23;
                return 60;

            case 23:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 24;
                return 68;

            case 24:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(27);
                this.state = 25;
                break;
            case 25:
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("part"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 26;
                return -3;

            case 26:
                this.bsCommit(27);
                this.state = 27;
                continue;

            case 27:
                this.bsMark(30);
                this.state = 28;
                break;
            case 28:
                this.setArgs();
                this.state = 29;
                return 59;

            case 29:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(30);
                this.state = 30;
                continue;

            case 30:
                this.bsCommit(31);
                this.state = 31;
                continue;

            case 31:
                this.bsCommit(32);
                this.state = 32;
                continue;

            case 32:
                this.bsCommit(33);
                this.state = 33;
                continue;

            case 33:
                this.bsCommit(34);
                this.state = 34;
                continue;

            case 34:
                return -1;

            }
        }
    }

    private int _26_sigil_tag_use() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(100))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 68;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("use"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                return -3;

            case 3:
                this.bsMark(18);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 57;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(17);
                this.state = 6;
                break;
            case 6:
                this.bsFailMark(10);
                this.bsMark(8);
                this.setArgs();
                this.state = 7;
                return 41;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 8:
                this.setArgs();
                this.state = 9;
                return 29;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 10;
                continue;

            case 10:
                this.bsMark(13);
                this.state = 11;
                break;
            case 11:
                this.setArgs();
                this.state = 12;
                return 57;

            case 12:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(13);
                this.state = 13;
                continue;

            case 13:
                this.bsMark(16);
                this.state = 14;
                break;
            case 14:
                this.setArgs();
                this.state = 15;
                return 59;

            case 15:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 16:
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 18:
                return -1;

            }
        }
    }

    private int _27_sigil_tag_separator() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(101))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 68;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("separator"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                return -3;

            case 3:
                this.bsMark(23);
                this.state = 4;
                break;
            case 4:
                this.bsMark(7);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 57;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                this.bsMark(22);
                this.state = 8;
                break;
            case 8:
                this.setArgs();
                this.state = 9;
                return 59;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 10;
                return 3;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(21);
                this.state = 11;
                break;
            case 11:
                if (!(this.lookahead(102))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 12;
                return 58;

            case 12:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 13;
                return 60;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 14;
                return 68;

            case 14:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(17);
                this.state = 15;
                break;
            case 15:
                this.startToken(CroTemplateTokenTypes.DECL_OPENER);
                if (!(this.literal("separator"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 16;
                return -3;

            case 16:
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsMark(20);
                this.state = 18;
                break;
            case 18:
                this.setArgs();
                this.state = 19;
                return 59;

            case 19:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(20);
                this.state = 20;
                continue;

            case 20:
                this.bsCommit(21);
                this.state = 21;
                continue;

            case 21:
                this.bsCommit(22);
                this.state = 22;
                continue;

            case 22:
                this.bsCommit(23);
                this.state = 23;
                continue;

            case 23:
                return -1;

            }
        }
    }

    private int _28_sigil_tag_comment() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(103))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.startToken(CroTemplateTokenTypes.COMMENT);
                if (!(this.literal("<#>"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(9);
                this.state = 1;
                break;
            case 1:
                this.bsFailMark(8);
                this.bsMark(4);
                this.bsFailMark(3);
                this.state = 2;
                break;
            case 2:
                if (!(this.wordChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(3);
                ++rep;
                this.bsCommit(3);
                this.bsMark(3, rep);
                this.state = 2;
                continue;

            case 3:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 4:
                this.bsMark(7);
                this.bsFailMark(6);
                this.state = 5;
                break;
            case 5:
                if (!(this.spaceChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(6);
                ++rep;
                this.bsCommit(6);
                this.bsMark(6, rep);
                this.state = 5;
                continue;

            case 6:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 7:
                if (this.lookahead(104)) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                if (!(this.anyChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 8;
                continue;

            case 8:
                rep = this.peekRep(9);
                ++rep;
                this.bsCommit(9);
                this.bsMark(9, rep);
                this.state = 1;
                continue;

            case 9:
                this.bsMark(11);
                this.state = 10;
                break;
            case 10:
                if (!(this.literal("</#>"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.state = 12;
                return -3;

            case 12:
                return -1;

            }
        }
    }

    private int _29_module_name() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.MODULE_NAME);
                this.setArgs();
                this.state = 1;
                return 70;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(6);
                this.state = 2;
                break;
            case 2:
                if (!(this.literal("::"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(5);
                this.state = 3;
                break;
            case 3:
                this.setArgs();
                this.state = 4;
                return 70;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(5);
                this.state = 5;
                continue;

            case 5:
                rep = this.peekRep(6);
                ++rep;
                this.bsCommit(6);
                this.bsMark(6, rep);
                this.state = 2;
                continue;

            case 6:
                this.state = 7;
                return -3;

            case 7:
                return -1;

            }
        }
    }

    private int _30_sigil_tag_apply() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(105))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 1;
                return 58;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 69;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(27);
                this.state = 3;
                break;
            case 3:
                this.startToken(CroTemplateTokenTypes.MACRO_NAME);
                this.setArgs();
                this.state = 4;
                return 70;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 5;
                return -3;

            case 5:
                this.bsMark(8);
                this.state = 6;
                break;
            case 6:
                this.setArgs();
                this.state = 7;
                return 57;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsMark(11);
                this.state = 9;
                break;
            case 9:
                this.setArgs();
                this.state = 10;
                return 34;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 57;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsMark(26);
                this.state = 15;
                break;
            case 15:
                this.setArgs();
                this.state = 16;
                return 59;

            case 16:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 17;
                return 3;

            case 17:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(25);
                this.state = 18;
                break;
            case 18:
                if (!(this.lookahead(106))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 19;
                return 58;

            case 19:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 20;
                return 60;

            case 20:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 21;
                return 69;

            case 21:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(24);
                this.state = 22;
                break;
            case 22:
                this.setArgs();
                this.state = 23;
                return 59;

            case 23:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(24);
                this.state = 24;
                continue;

            case 24:
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 25:
                this.bsCommit(26);
                this.state = 26;
                continue;

            case 26:
                this.bsCommit(27);
                this.state = 27;
                continue;

            case 27:
                return -1;

            }
        }
    }

    private int _31_signature() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.OPEN_PAREN);
                if (!(this.literal("("))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.setArgs();
                this.state = 2;
                return 56;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(19);
                this.state = 3;
                break;
            case 3:
                this.bsMark(11);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 32;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 6;
                return 56;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(10);
                this.state = 7;
                break;
            case 7:
                this.startToken(CroTemplateTokenTypes.COMMA);
                if (!(this.literal(","))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 8;
                return -3;

            case 8:
                this.setArgs();
                this.state = 9;
                return 56;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 10:
                rep = this.peekRep(11);
                ++rep;
                this.bsCommit(11);
                this.bsMark(11, rep);
                this.state = 4;
                continue;

            case 11:
                this.setArgs();
                this.state = 12;
                return 56;

            case 12:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(18);
                this.state = 13;
                break;
            case 13:
                this.startToken(CroTemplateTokenTypes.CLOSE_PAREN);
                if (!(this.literal(")"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 14;
                return -3;

            case 14:
                this.bsMark(17);
                this.state = 15;
                break;
            case 15:
                this.setArgs();
                this.state = 16;
                return 57;

            case 16:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 18:
                this.bsCommit(19);
                this.state = 19;
                continue;

            case 19:
                return -1;

            }
        }
    }

    private int _32_parameter() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(7);
                this.bsMark(5);
                this.startToken(CroTemplateTokenTypes.NAMED_PARAMETER_SYNTAX);
                if (!(this.literal(":"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.bsMark(4);
                this.state = 2;
                break;
            case 2:
                this.setArgs();
                this.state = 3;
                return 33;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(4);
                this.state = 4;
                continue;

            case 4:
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 5:
                this.setArgs();
                this.state = 6;
                return 33;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 7;
                continue;

            case 7:
                this.bsMark(15);
                this.state = 8;
                break;
            case 8:
                if (!(this.lookahead(107))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 9;
                return 56;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.startToken(CroTemplateTokenTypes.DEFAULT_PARAMETER_SYNTAX);
                if (!(this.literal("="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 10;
                return -3;

            case 10:
                this.setArgs();
                this.state = 11;
                return 56;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.setArgs();
                this.state = 13;
                return 36;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsCommit(15);
                this.state = 15;
                continue;

            case 15:
                return -1;

            }
        }
    }

    private int _33_parameter_name() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.VARIABLE_NAME);
                if (!(this.literal("$"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(3);
                this.state = 1;
                break;
            case 1:
                this.setArgs();
                this.state = 2;
                return 70;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(3);
                this.state = 3;
                continue;

            case 3:
                this.state = 4;
                return -3;

            case 4:
                return -1;

            }
        }
    }

    private int _34_arglist() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.OPEN_PAREN);
                if (!(this.literal("("))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.setArgs();
                this.state = 2;
                return 56;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(19);
                this.state = 3;
                break;
            case 3:
                this.bsMark(11);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 35;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 6;
                return 56;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(10);
                this.state = 7;
                break;
            case 7:
                this.startToken(CroTemplateTokenTypes.COMMA);
                if (!(this.literal(","))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 8;
                return -3;

            case 8:
                this.setArgs();
                this.state = 9;
                return 56;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 10:
                rep = this.peekRep(11);
                ++rep;
                this.bsCommit(11);
                this.bsMark(11, rep);
                this.state = 4;
                continue;

            case 11:
                this.setArgs();
                this.state = 12;
                return 56;

            case 12:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(18);
                this.state = 13;
                break;
            case 13:
                this.startToken(CroTemplateTokenTypes.CLOSE_PAREN);
                if (!(this.literal(")"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 14;
                return -3;

            case 14:
                this.bsMark(17);
                this.state = 15;
                break;
            case 15:
                this.setArgs();
                this.state = 16;
                return 57;

            case 16:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsCommit(18);
                this.state = 18;
                continue;

            case 18:
                this.bsCommit(19);
                this.state = 19;
                continue;

            case 19:
                return -1;

            }
        }
    }

    private int _35_argument() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(20);
                this.bsMark(18);
                this.startToken(CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX);
                if (!(this.literal(":"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.bsMark(4);
                this.state = 2;
                break;
            case 2:
                this.startToken(CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX);
                if (!(this.literal("!"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                return -3;

            case 3:
                this.bsCommit(4);
                this.state = 4;
                continue;

            case 4:
                this.bsMark(17);
                this.state = 5;
                break;
            case 5:
                this.startToken(CroTemplateTokenTypes.NAMED_ARGUMENT_NAME);
                this.setArgs();
                this.state = 6;
                return 70;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 7;
                return -3;

            case 7:
                this.bsMark(16);
                this.state = 8;
                break;
            case 8:
                this.startToken(CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX);
                if (!(this.literal("("))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 9;
                return -3;

            case 9:
                this.bsMark(15);
                this.state = 10;
                break;
            case 10:
                this.setArgs();
                this.state = 11;
                return 36;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(14);
                this.state = 12;
                break;
            case 12:
                this.startToken(CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX);
                if (!(this.literal(")"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 13;
                return -3;

            case 13:
                this.bsCommit(14);
                this.state = 14;
                continue;

            case 14:
                this.bsCommit(15);
                this.state = 15;
                continue;

            case 15:
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 16:
                this.bsCommit(17);
                this.state = 17;
                continue;

            case 17:
                this.bsCommit(20);
                this.state = 20;
                continue;

            case 18:
                this.setArgs();
                this.state = 19;
                return 36;

            case 19:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 20;
                continue;

            case 20:
                return -1;

            }
        }
    }

    private int _36_expression() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.declareDynamicVariable("$*LEFTSIGIL", "");
                this.declareDynamicVariable("$*PREC", "");
                this.declareDynamicVariable("$*SUB_PREC", "");
                this.declareDynamicVariable("$*ASSOC", "");
                this.declareDynamicVariable("$*NEXT_TERM", "");
                this.declareDynamicVariable("$*FAKE", 0);
                this.setArgs();
                this.state = 1;
                return 40;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(12);
                this.state = 2;
                break;
            case 2:
                if (!(this.lookahead(108))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 3;
                return 56;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.assignDynamicVariable("$*NEXT_TERM", "");
                this.setArgs();
                this.state = 4;
                return 38;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.precInfoToken();
                this.state = 5;
                return -3;

            case 5:
                this.bsMark(8);
                this.state = 6;
                break;
            case 6:
                if (this.lookahead(109)) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 7;
                return 56;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsMark(11);
                this.state = 9;
                break;
            case 9:
                this.setArgs();
                this.state = 10;
                return 37;

            case 10:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(11);
                this.state = 11;
                continue;

            case 11:
                rep = this.peekRep(12);
                ++rep;
                this.bsCommit(12);
                this.bsMark(12, rep);
                this.state = 2;
                continue;

            case 12:
                this.bsFailMark(15);
                this.bsMark(14);
                if (this.lookahead(110)) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.startToken(CroTemplateTokenTypes.END_OF_EXPR);
                this.state = 13;
                return -3;

            case 13:
                this.bsCommit(15);
                this.state = 15;
                continue;

            case 14:
                this.state = 15;
                continue;

            case 15:
                return -1;

            }
        }
    }

    private int _37_nextterm() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.setArgs();
                this.state = 1;
                return 40;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                return -1;

            }
        }
    }

    private int _38_infixish() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.setArgs();
                this.state = 1;
                return 39;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                return -1;

            }
        }
    }

    private int _39_infix() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.INFIX);
                this.bsFailMark(25);
                this.bsMark(1);
                if (!(this.literal("!==="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 1:
                this.bsMark(2);
                if (!(this.literal("==="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 2:
                this.bsMark(3);
                if (!(this.literal("and"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "d=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 3:
                this.bsMark(4);
                if (!(this.literal("=="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 4:
                this.bsMark(5);
                if (!(this.literal("!="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 5:
                this.bsMark(6);
                if (!(this.literal("<="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 6:
                this.bsMark(7);
                if (!(this.literal(">="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 7:
                this.bsMark(8);
                if (!(this.literal("eq"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 8:
                this.bsMark(9);
                if (!(this.literal("ne"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 9:
                this.bsMark(10);
                if (!(this.literal("le"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 10:
                this.bsMark(11);
                if (!(this.literal("ge"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 11:
                this.bsMark(12);
                if (!(this.literal("lt"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 12:
                this.bsMark(13);
                if (!(this.literal("gt"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 13:
                this.bsMark(14);
                if (!(this.literal("&&"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "l=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 14:
                this.bsMark(15);
                if (!(this.literal("||"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "k=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 15:
                this.bsMark(16);
                if (!(this.literal("or"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "c=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 16:
                this.bsMark(17);
                if (!(this.literal("*"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "u=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 17:
                this.bsMark(18);
                if (!(this.literal("/"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "u=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 18:
                this.bsMark(19);
                if (!(this.literal("%"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "u=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 19:
                this.bsMark(20);
                if (!(this.literal("+"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "t=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 20:
                this.bsMark(21);
                if (!(this.literal("-"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "t=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 21:
                this.bsMark(22);
                if (!(this.literal("x"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "s=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 22:
                this.bsMark(23);
                if (!(this.literal("~"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "r=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 23:
                this.bsMark(24);
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.bsCommit(25);
                this.state = 25;
                continue;

            case 24:
                if (!(this.literal(">"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.assignDynamicVariable("$*PREC", "m=");
                this.assignDynamicVariable("$*ASSOC", "left");
                this.state = 25;
                continue;

            case 25:
                this.state = 26;
                return -3;

            case 26:
                return -1;

            }
        }
    }

    private int _40_term() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(16);
                this.bsMark(2);
                this.setArgs();
                this.state = 1;
                return 41;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 2:
                this.bsMark(4);
                this.setArgs();
                this.state = 3;
                return 44;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 4:
                this.bsMark(6);
                this.setArgs();
                this.state = 5;
                return 43;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 6:
                this.bsMark(8);
                this.setArgs();
                this.state = 7;
                return 42;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 8:
                this.bsMark(10);
                this.setArgs();
                this.state = 9;
                return 45;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 10:
                this.bsMark(12);
                this.setArgs();
                this.state = 11;
                return 46;

            case 11:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 12:
                this.bsMark(14);
                this.setArgs();
                this.state = 13;
                return 47;

            case 13:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(16);
                this.state = 16;
                continue;

            case 14:
                this.setArgs();
                this.state = 15;
                return 48;

            case 15:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 16;
                continue;

            case 16:
                return -1;

            }
        }
    }

    private int _41_single_quote_string() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.STRING_QUOTE_SINGLE);
                if (!(this.literal("'"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.bsMark(6);
                this.state = 2;
                break;
            case 2:
                this.startToken(CroTemplateTokenTypes.STRING_TEXT);
                this.bsFailMark(4);
                this.state = 3;
                break;
            case 3:
                if (!(this.notInCharList("'"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 3;
                continue;

            case 4:
                this.state = 5;
                return -3;

            case 5:
                this.bsCommit(6);
                this.state = 6;
                continue;

            case 6:
                this.bsMark(9);
                this.state = 7;
                break;
            case 7:
                this.startToken(CroTemplateTokenTypes.STRING_QUOTE_SINGLE);
                if (!(this.literal("'"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 8;
                return -3;

            case 8:
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                return -1;

            }
        }
    }

    private int _42_int() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.INT_LITERAL);
                this.bsMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.literal("-"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(2);
                this.state = 2;
                continue;

            case 2:
                this.bsFailMark(4);
                this.state = 3;
                break;
            case 3:
                if (!(this.digitChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 3;
                continue;

            case 4:
                this.state = 5;
                return -3;

            case 5:
                return -1;

            }
        }
    }

    private int _43_rat() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.RAT_LITERAL);
                this.bsMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.literal("-"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(2);
                this.state = 2;
                continue;

            case 2:
                this.bsMark(4);
                this.state = 3;
                break;
            case 3:
                if (!(this.digitChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 3;
                continue;

            case 4:
                if (!(this.literal("."))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsFailMark(6);
                this.state = 5;
                break;
            case 5:
                if (!(this.digitChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(6);
                ++rep;
                this.bsCommit(6);
                this.bsMark(6, rep);
                this.state = 5;
                continue;

            case 6:
                this.state = 7;
                return -3;

            case 7:
                return -1;

            }
        }
    }

    private int _44_num() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.NUM_LITERAL);
                this.bsMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.literal("-"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(2);
                this.state = 2;
                continue;

            case 2:
                this.bsMark(4);
                this.state = 3;
                break;
            case 3:
                if (!(this.digitChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 3;
                continue;

            case 4:
                if (!(this.literal("."))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsFailMark(6);
                this.state = 5;
                break;
            case 5:
                if (!(this.digitChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(6);
                ++rep;
                this.bsCommit(6);
                this.bsMark(6, rep);
                this.state = 5;
                continue;

            case 6:
                if (!(this.inCharList("eE"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(8);
                this.state = 7;
                break;
            case 7:
                if (!(this.literal("-"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsFailMark(10);
                this.state = 9;
                break;
            case 9:
                if (!(this.digitChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(10);
                ++rep;
                this.bsCommit(10);
                this.bsMark(10, rep);
                this.state = 9;
                continue;

            case 10:
                this.state = 11;
                return -3;

            case 11:
                return -1;

            }
        }
    }

    private int _45_bool() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.BOOL_LITERAL);
                this.bsFailMark(2);
                this.bsMark(1);
                if (!(this.literal("True"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(2);
                this.state = 2;
                continue;

            case 1:
                if (!(this.literal("False"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 2;
                continue;

            case 2:
                this.state = 3;
                return -3;

            case 3:
                return -1;

            }
        }
    }

    private int _46_variable() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.VARIABLE_NAME);
                if (!(this.literal("$"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsMark(3);
                this.state = 1;
                break;
            case 1:
                this.setArgs();
                this.state = 2;
                return 70;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(3);
                this.state = 3;
                continue;

            case 3:
                this.state = 4;
                return -3;

            case 4:
                this.bsMark(10);
                this.state = 5;
                break;
            case 5:
                this.setArgs();
                this.state = 6;
                return 61;

            case 6:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(9);
                this.state = 7;
                break;
            case 7:
                this.setArgs();
                this.state = 8;
                return 49;

            case 8:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 10:
                return -1;

            }
        }
    }

    private int _47_deref_term() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.setArgs();
                this.state = 1;
                return 61;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(4);
                this.state = 2;
                break;
            case 2:
                this.setArgs();
                this.state = 3;
                return 49;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(4);
                this.state = 4;
                continue;

            case 4:
                return -1;

            }
        }
    }

    private int _48_parenthesized_expression() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.OPEN_PAREN);
                if (!(this.literal("("))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.setArgs();
                this.state = 2;
                return 56;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(9);
                this.state = 3;
                break;
            case 3:
                this.setArgs();
                this.state = 4;
                return 36;

            case 4:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 5;
                return 56;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(8);
                this.state = 6;
                break;
            case 6:
                this.startToken(CroTemplateTokenTypes.CLOSE_PAREN);
                if (!(this.literal(")"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 7;
                return -3;

            case 7:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                return -1;

            }
        }
    }

    private int _49_deref() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.setArgs();
                this.state = 1;
                return 50;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(7);
                this.state = 2;
                break;
            case 2:
                this.setArgs();
                this.state = 3;
                return 61;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(6);
                this.state = 4;
                break;
            case 4:
                this.setArgs();
                this.state = 5;
                return 50;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(6);
                this.state = 6;
                continue;

            case 6:
                rep = this.peekRep(7);
                ++rep;
                this.bsCommit(7);
                this.bsMark(7, rep);
                this.state = 2;
                continue;

            case 7:
                return -1;

            }
        }
    }

    private int _50_deref_item() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(10);
                this.bsMark(2);
                this.setArgs();
                this.state = 1;
                return 51;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 2:
                this.bsMark(4);
                this.setArgs();
                this.state = 3;
                return 52;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 4:
                this.bsMark(6);
                this.setArgs();
                this.state = 5;
                return 54;

            case 5:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 6:
                this.bsMark(8);
                this.setArgs();
                this.state = 7;
                return 55;

            case 7:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsCommit(10);
                this.state = 10;
                continue;

            case 8:
                this.setArgs();
                this.state = 9;
                return 53;

            case 9:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 10;
                continue;

            case 10:
                return -1;

            }
        }
    }

    private int _51_deref_item_method() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                if (!(this.lookahead(111))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.startToken(CroTemplateTokenTypes.IDENTIFER);
                this.setArgs();
                this.state = 1;
                return 70;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 2;
                return -3;

            case 2:
                this.setArgs();
                this.state = 3;
                return 34;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                return -1;

            }
        }
    }

    private int _52_deref_item_smart() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.IDENTIFER);
                this.setArgs();
                this.state = 1;
                return 70;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.state = 2;
                return -3;

            case 2:
                return -1;

            }
        }
    }

    private int _53_deref_item_hash_literal() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.OPEN_ANGLE);
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.bsMark(9);
                this.state = 2;
                break;
            case 2:
                this.startToken(CroTemplateTokenTypes.LITERAL_KEY);
                this.bsMark(4);
                this.state = 3;
                break;
            case 3:
                if (!(this.notInCharList(">"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 3;
                continue;

            case 4:
                this.state = 5;
                return -3;

            case 5:
                this.bsMark(8);
                this.state = 6;
                break;
            case 6:
                this.startToken(CroTemplateTokenTypes.CLOSE_ANGLE);
                if (!(this.literal(">"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 7;
                return -3;

            case 7:
                this.bsCommit(8);
                this.state = 8;
                continue;

            case 8:
                this.bsCommit(9);
                this.state = 9;
                continue;

            case 9:
                return -1;

            }
        }
    }

    private int _54_deref_item_array() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.OPEN_BRACKET);
                if (!(this.literal("["))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.bsMark(7);
                this.state = 2;
                break;
            case 2:
                this.setArgs();
                this.state = 3;
                return 36;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(6);
                this.state = 4;
                break;
            case 4:
                this.startToken(CroTemplateTokenTypes.CLOSE_BRACKET);
                if (!(this.literal("]"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 5;
                return -3;

            case 5:
                this.bsCommit(6);
                this.state = 6;
                continue;

            case 6:
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                return -1;

            }
        }
    }

    private int _55_deref_item_hash() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.OPEN_CURLY);
                if (!(this.literal("{"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                this.bsMark(7);
                this.state = 2;
                break;
            case 2:
                this.setArgs();
                this.state = 3;
                return 36;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(6);
                this.state = 4;
                break;
            case 4:
                this.startToken(CroTemplateTokenTypes.CLOSE_CURLY);
                if (!(this.literal("}"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 5;
                return -3;

            case 5:
                this.bsCommit(6);
                this.state = 6;
                continue;

            case 6:
                this.bsCommit(7);
                this.state = 7;
                continue;

            case 7:
                return -1;

            }
        }
    }

    private int _56_ws() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(5);
                this.bsMark(4);
                this.startToken(CroTemplateTokenTypes.SYNTAX_WHITE_SPACE);
                this.bsFailMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.spaceChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(2);
                ++rep;
                this.bsCommit(2);
                this.bsMark(2, rep);
                this.state = 1;
                continue;

            case 2:
                this.state = 3;
                return -3;

            case 3:
                this.bsCommit(5);
                this.state = 5;
                continue;

            case 4:
                this.state = 5;
                continue;

            case 5:
                return -1;

            }
        }
    }

    private int _57_hws() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.SYNTAX_WHITE_SPACE);
                this.bsFailMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.inCharList("\u0009 \u00A0\u1680\u180E\u2002\u2003\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(2);
                ++rep;
                this.bsCommit(2);
                this.bsMark(2, rep);
                this.state = 1;
                continue;

            case 2:
                this.state = 3;
                return -3;

            case 3:
                return -1;

            }
        }
    }

    private int _58_tlt() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_OPEN);
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _59_tgt() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_CLOSE);
                if (!(this.literal(">"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _60_tclose() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_SLASH);
                if (!(this.literal("/"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _61_dot() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.DOT);
                if (!(this.literal("."))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _62_lt() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.LITERAL_TAG_OPEN);
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _63_gt() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.LITERAL_TAG_CLOSE);
                if (!(this.literal(">"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _64_close() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.LITERAL_TAG_SLASH);
                if (!(this.literal("/"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _65_iter_sigil() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_ITER_SIGIL);
                if (!(this.literal("@"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _66_cond_sigil() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL);
                if (!(this.inCharList("?!"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _67_call_sigil() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_CALL_SIGIL);
                if (!(this.literal("&"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _68_decl_sigil() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_DECL_SIGIL);
                if (!(this.literal(":"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _69_apply_sigil() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.TEMPLATE_TAG_APPLY_SIGIL);
                if (!(this.literal("|"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 1;
                return -3;

            case 1:
                return -1;

            }
        }
    }

    private int _70_identifier() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.setArgs();
                this.state = 1;
                return 71;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.bsMark(4);
                this.state = 2;
                break;
            case 2:
                if (!(this.inCharList("-'"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.setArgs();
                this.state = 3;
                return 71;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 2;
                continue;

            case 4:
                return -1;

            }
        }
    }

    private int _71_ident() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.bsFailMark(2);
                this.bsMark(1);
                if (!(this.alphaChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(2);
                this.state = 2;
                continue;

            case 1:
                if (!(this.literal("_"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 2;
                continue;

            case 2:
                this.bsMark(4);
                this.state = 3;
                break;
            case 3:
                if (!(this.wordChar())) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(4);
                ++rep;
                this.bsCommit(4);
                this.bsMark(4, rep);
                this.state = 3;
                continue;

            case 4:
                return -1;

            }
        }
    }

    private int ___lookahead_0() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("-->"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_1() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                if (this.lookahead(74)) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                if (this.lookahead(75)) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_2() {
        while (true) {
            if (this.state == 0) {
                if (!(this.inCharList("/"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_3() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 11;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                return -1;

            }
        }
    }

    private int ___lookahead_4() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                if (this.lookahead(77)) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_5() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 11;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                return -1;

            }
        }
    }

    private int ___lookahead_6() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<."))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_7() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<$"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_8() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<@"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_9() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.bsMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.inCharList("\u0009 \u00A0\u1680\u180E\u2002\u2003\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(2);
                ++rep;
                this.bsCommit(2);
                this.bsMark(2, rep);
                this.state = 1;
                continue;

            case 2:
                if (!(this.literal(":"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                return -1;

            }
        }
    }

    private int ___lookahead_10() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.bsMark(2);
                this.state = 1;
                break;
            case 1:
                if (!(this.inCharList("\u0009 \u00A0\u1680\u180E\u2002\u2003\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                rep = this.peekRep(2);
                ++rep;
                this.bsCommit(2);
                this.bsMark(2, rep);
                this.state = 1;
                continue;

            case 2:
                if (!(this.literal("$"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                return -1;

            }
        }
    }

    private int ___lookahead_11() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</@"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_12() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                if (!(this.inCharList("?!"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_13() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                if (!(this.inCharList("?!"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_14() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 56;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                if (!(this.literal("<!?"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                return -1;

            }
        }
    }

    private int ___lookahead_15() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 56;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                if (!(this.literal("<!"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsFailMark(3);
                this.bsMark(2);
                if (!(this.literal(">"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(3);
                this.state = 3;
                continue;

            case 2:
                if (!(this.inCharList("\u0009 \u00A0\u1680\u180E\u2002\u2003\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 3;
                continue;

            case 3:
                return -1;

            }
        }
    }

    private int ___lookahead_16() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<!?"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_17() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</?"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_18() {
        while (true) {
            switch (this.state) {
            case 0:
                if (!(this.literal("<!"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsFailMark(2);
                this.bsMark(1);
                if (!(this.literal(">"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.bsCommit(2);
                this.state = 2;
                continue;

            case 1:
                if (!(this.inCharList("\u0009 \u00A0\u1680\u180E\u2002\u2003\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                this.state = 2;
                continue;

            case 2:
                return -1;

            }
        }
    }

    private int ___lookahead_19() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</!"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_20() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<&"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_21() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<:sub"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_22() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</:"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_23() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<:macro"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_24() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</:"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_25() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<:body"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_26() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<:part"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_27() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</:"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_28() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<:use"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_29() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<:separator"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_30() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</:"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_31() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<#>"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_32() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</#>"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_33() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("<|"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_34() {
        while (true) {
            if (this.state == 0) {
                if (!(this.literal("</|"))) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_35() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 56;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                if (!(this.literal("="))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                return -1;

            }
        }
    }

    private int ___lookahead_36() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 56;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 2;
                return 38;

            case 2:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                this.setArgs();
                this.state = 3;
                return 56;

            case 3:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                return -1;

            }
        }
    }

    private int ___lookahead_37() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 56;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                if (!(this.inCharList("]})"))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                return -1;

            }
        }
    }

    private int ___lookahead_38() {
        while (true) {
            if (this.state == 0) {
                if (!(this.endOfString())) {
                    if (this.backtrack()) {
                        continue;
                    }
                    else {
                        return -2;
                    }
                }
                return -1;
            }
        }
    }

    private int ___lookahead_39() {
        while (true) {
            switch (this.state) {
            case 0:
                this.setArgs();
                this.state = 1;
                return 70;

            case 1:
                if (this.lastResult.isFailed()) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                } else {
                    this.pos = this.lastResult.getPos();
                }
                if (!(this.literal("("))) {
                    if (this.backtrack()) {
                        continue;
                    } else {
                        return -2;
                    }
                }
                return -1;

            }
        }
    }

    @Override
    public MAINBraid createInstance() {
        return new MAINBraid();
    }

}
