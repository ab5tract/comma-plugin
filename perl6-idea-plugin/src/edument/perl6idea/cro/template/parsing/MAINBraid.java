package edument.perl6idea.cro.template.parsing;
import edument.perl6idea.parsing.Cursor;

public class MAINBraid extends Cursor<MAINBraid> {

    public int runRule() {
        int result;
        switch (this.ruleNumber) {
        case 0:
            result = this._0_TOP();
            break;
        case 1:
            result = this._1_bogus_end();
            break;
        case 2:
            result = this._2_sequence_element();
            break;
        case 3:
            result = this._3_sequence_element_literal_text();
            break;
        case 4:
            result = this._4_sigil_tag();
            break;
        case 5:
            result = this._5_sigil_tag_use();
            break;
        case 6:
            result = this._6_hws();
            break;
        default:
            throw new RuntimeException();

        }
        return result;
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
                this.bsFailMark(4);
                this.bsMark(2);
                this.setArgs();
                this.state = 1;
                return 3;

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
                this.bsCommit(4);
                this.state = 4;
                continue;

            case 2:
                this.setArgs();
                this.state = 3;
                return 4;

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
                this.state = 4;
                continue;

            case 4:
                return -1;

            }
        }
    }

    private int _3_sequence_element_literal_text() {
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

    private int _4_sigil_tag() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.setArgs();
                this.state = 1;
                return 5;

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

    private int _5_sigil_tag_use() {
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.USE_OPENER);
                if (!(this.literal("<:use"))) {
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
                return 6;

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

    private int _6_hws() {
        int rep;
        while (true) {
            switch (this.state) {
            case 0:
                this.checkArgs(0);
                this.startToken(CroTemplateTokenTypes.WHITE_SPACE);
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

    public MAINBraid createInstance() {
        return new MAINBraid();
    }

}
