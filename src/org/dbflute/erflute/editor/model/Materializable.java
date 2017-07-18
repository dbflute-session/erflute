package org.dbflute.erflute.editor.model;

/**
 * TODO ymd 非常に大きな変更につながるため、レビューした方が良いと思う。
 * このインターフェースを実装するクラスは、サブクラスが何か(実体か、仮想か)について知っている必要がある。
 * これは設計ミスではないか？代替策があれば、それに変える。
 * @author ymd
 */
public interface Materializable {
    /**
     * ERVirtualTabel等の仮想概念用拡張ポイント。
     * @return 自身が実体なら自身を返す。仮想概念ならその実体を返す。
     */
    default ViewableModel toMaterialize() {
        return (ViewableModel) this;
    }

    default boolean sameMaterial(Materializable materialable) {
        if (materialable == null) {
            return false;
        }

        return toMaterialize().equals(materialable.toMaterialize());
    }
}
