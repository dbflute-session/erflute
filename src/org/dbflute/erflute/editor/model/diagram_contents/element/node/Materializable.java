package org.dbflute.erflute.editor.model.diagram_contents.element.node;

/**
 * TODO ymd 非常に大きな変更につながるため、レビューした方が良いと思う。
 * このインターフェースを実装するクラスは、サブクラスが何か(実体か、仮想か)について知っている必要がある。
 * これは設計ミスではないか？代替策があれば、それに変える。
 * @author ymd
 * @param <TEntity> 仮想概念が参照する実体の型。
 */
public interface Materializable<TEntity> {
    /**
     * ERVirtualTabel等の仮想概念用拡張ポイント。
     * @return 自身が実体なら自身を返す。仮想概念ならその実体を返す。
     */
    @SuppressWarnings("unchecked")
    default TEntity toMaterialize() {
        return (TEntity) this;
    }
}
